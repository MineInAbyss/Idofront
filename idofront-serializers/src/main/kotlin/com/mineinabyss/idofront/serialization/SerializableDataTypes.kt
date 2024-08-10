package com.mineinabyss.idofront.serialization

import com.destroystokyo.paper.profile.PlayerProfile
import io.papermc.paper.block.BlockPredicate
import io.papermc.paper.datacomponent.item.MapDecorations.DecorationEntry
import io.papermc.paper.datacomponent.item.Tool.Rule
import io.papermc.paper.datacomponent.DataComponentType
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.*
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.TypedKey
import io.papermc.paper.registry.set.RegistrySet
import io.papermc.paper.registry.tag.TagKey
import kotlinx.serialization.Serializable
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.util.TriState
import org.bukkit.Color
import org.bukkit.Registry
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ItemType
import org.bukkit.inventory.meta.trim.ArmorTrim
import org.bukkit.map.MapCursor
import org.bukkit.potion.PotionEffect

object SerializableDataTypes {

    fun <T> setData(itemStack: ItemStack, dataComponent: DataComponentType.Valued<T>, any: T?) {
        any?.let { itemStack.setData(dataComponent, any) }
    }

    fun <T> setData(itemStack: ItemStack, dataComponent: DataComponentType.NonValued, any: T?) {
        any?.let { itemStack.setData(dataComponent) }
    }

    interface DataType {
        fun setDataType(itemStack: ItemStack)
    }

    @Serializable
    data class Unbreakable(val shownInTooltip: Boolean = true) : DataType {
        constructor(unbreakable: io.papermc.paper.datacomponent.item.Unbreakable) : this(unbreakable.showInTooltip())

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.UNBREAKABLE,
                io.papermc.paper.datacomponent.item.Unbreakable.unbreakable(shownInTooltip)
            )
        }
    }

    @Serializable
    data class PotionContents(
        val potionType: @Serializable(PotionTypeSerializer::class) org.bukkit.potion.PotionType?,
        val color: @Serializable(ColorSerializer::class) Color?,
        val customEffects: List<@Serializable(PotionEffectSerializer::class) PotionEffect> = emptyList()
    ) : DataType {

        constructor(potionContents: io.papermc.paper.datacomponent.item.PotionContents) : this(
            potionContents.potion(),
            potionContents.customColor(),
            potionContents.customEffects()
        )

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.POTION_CONTENTS,
                io.papermc.paper.datacomponent.item.PotionContents.potionContents().potion(potionType).customColor(color)
                    .addCustomEffects(customEffects).build()
            )
        }
    }

    @Serializable
    data class Enchantments(
        val enchantments: List<SerializableEnchantment>,
        val showInToolTip: Boolean = true
    ) : DataType {
        constructor(itemEnchantments: ItemEnchantments) : this(
            itemEnchantments.enchantments().map(::SerializableEnchantment), itemEnchantments.showInTooltip()
        )

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.ENCHANTMENTS,
                ItemEnchantments.itemEnchantments(enchantments.associate { it.enchant to it.level }, showInToolTip)
            )
        }
    }

    @Serializable
    data class StoredEnchantments(
        val enchantments: List<SerializableEnchantment>,
        val showInToolTip: Boolean = true
    ) : DataType {
        constructor(itemEnchantments: ItemEnchantments) : this(
            itemEnchantments.enchantments().map(::SerializableEnchantment), itemEnchantments.showInTooltip()
        )

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.STORED_ENCHANTMENTS,
                ItemEnchantments.itemEnchantments(enchantments.associate { it.enchant to it.level }, showInToolTip)
            )
        }
    }

    @Serializable
    @JvmInline
    value class ChargedProjectiles(private val projectiles: List<SerializableItemStack>) : DataType {
        constructor(vararg projectiles: ItemStack) : this(projectiles.map { it.toSerializable() })
        constructor(projectiles: io.papermc.paper.datacomponent.item.ChargedProjectiles) : this(
            projectiles.projectiles().map { it.toSerializable() })

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.CHARGED_PROJECTILES,
                io.papermc.paper.datacomponent.item.ChargedProjectiles.chargedProjectiles(projectiles.mapNotNull { it.toItemStackOrNull() })
            )
        }
    }

    @Serializable
    @JvmInline
    value class BundleContent(private val contents: List<SerializableItemStack>) : DataType {
        constructor(vararg contents: ItemStack) : this(contents.map { it.toSerializable() })
        constructor(contents: BundleContents) : this(contents.contents().map { it.toSerializable() })

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.BUNDLE_CONTENTS,
                BundleContents.bundleContents(contents.mapNotNull { it.toItemStackOrNull() })
            )
        }
    }

    @Serializable
    data class WrittenBook(
        val title: String,
        val author: String,
        val generation: Int,
        val resolved: Boolean,
        val pages: List<@Serializable(MiniMessageSerializer::class) Component> = emptyList()
    ) : DataType {
        constructor(written: WrittenBookContent) : this(
            written.title().raw(),
            written.author(),
            written.generation(),
            written.resolved(),
            written.pages().map { it.raw() }
        )

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.WRITTEN_BOOK_CONTENT,
                WrittenBookContent.writtenBookContent(title, author).resolved(resolved).generation(generation)
                    .addPages(pages).build()
            )
        }
    }

    @Serializable
    data class WritableBook(val pages: List<String>) : DataType {
        constructor(writable: WritableBookContent) : this(writable.pages().map { it.raw() })

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.WRITABLE_BOOK_CONTENT,
                WritableBookContent.writeableBookContent().addPages(pages).build()
            )
        }
    }

    @Serializable
    data class MapDecoration(val type: MapCursor.Type, val x: Double, val z: Double, val rotation: Float) {
        constructor(entry: MapDecorations.DecorationEntry) : this(entry.type(), entry.x(), entry.z(), entry.rotation())
        val paperDecoration: DecorationEntry = DecorationEntry.of(type, x, z, rotation)

        companion object {
            fun toPaperDecorations(decorations: List<MapDecoration>) =
                decorations.mapIndexed { i, mapDecoration -> i.toString() to mapDecoration.paperDecoration }.toMap()
        }
    }

    @Serializable
    data class Tool(
        val rules: List<Rule> = emptyList(),
        val defaultMiningSpeed: Float,
        val damagePerBlock: Int
    ) : BlockTags(), DataType {
        constructor(tool: io.papermc.paper.datacomponent.item.Tool) : this(
            tool.rules().map(::Rule),
            tool.defaultMiningSpeed(),
            tool.damagePerBlock()
        )

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.TOOL, io.papermc.paper.datacomponent.item.Tool.tool()
                    .damagePerBlock(damagePerBlock)
                    .defaultMiningSpeed(defaultMiningSpeed)
                    .addRules(rules.toPaperRules())
                    .build()
            )
        }

        @Serializable
        data class Rule(
            val blockTypes: List<@Serializable(KeySerializer::class) Key>,
            val speed: Float? = null,
            val correctForDrops: TriState
        ) {
            constructor(rule: io.papermc.paper.datacomponent.item.Tool.Rule) : this(
                rule.blocks().map { it.key() },
                rule.speed(),
                rule.correctForDrops()
            )
        }
    }

    @Serializable
    data class CanPlaceOn(
        val showInToolTip: Boolean = true,
        val modifiers: List<BlockPredicate>
    ) : BlockTags(), DataType {
        constructor(predicate: ItemAdventurePredicate) : this(predicate.showInTooltip(), predicate.predicates().map(::BlockPredicate))

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.CAN_PLACE_ON, ItemAdventurePredicate.itemAdventurePredicate()
                    .showInTooltip(showInToolTip).apply {
                        modifiers.toPaperBlockPredicate().forEach { blockPredicate ->
                            addPredicate(blockPredicate)
                        }
                    }
                    .build()
            )
        }
    }

    @Serializable
    data class CanBreak(
        val showInToolTip: Boolean = true,
        val modifiers: List<BlockPredicate>
    ) : BlockTags(), DataType {
        constructor(predicate: ItemAdventurePredicate) : this(predicate.showInTooltip(), predicate.predicates().map(::BlockPredicate))

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.CAN_BREAK, ItemAdventurePredicate.itemAdventurePredicate()
                    .showInTooltip(showInToolTip).apply {
                        modifiers.toPaperBlockPredicate().forEach { blockPredicate ->
                            addPredicate(blockPredicate)
                        }
                    }
                    .build()
            )
        }
    }

    @Serializable
    sealed class BlockTags {
        fun List<Tool.Rule>.toPaperRules(): List<Rule> {
            val rules = mutableListOf<Rule>()

            this.forEach { rule ->
                val blockTagKeys =
                    rule.blockTypes.map { TagKey.create(RegistryKey.BLOCK, it) }.filter(Registry.BLOCK::hasTag)
                blockTagKeys.map(Registry.BLOCK::getTag).forEach { blockTag ->
                    rules += Rule.of(blockTag, rule.speed, rule.correctForDrops)
                }
                val blockKeys = rule.blockTypes.filter { Registry.BLOCK.get(it) != null }
                    .map { TypedKey.create(RegistryKey.BLOCK, it.key()) }
                val keySet = RegistrySet.keySet(RegistryKey.BLOCK, blockKeys)
                rules += Rule.of(keySet, rule.speed, rule.correctForDrops)
            }

            return rules
        }

        fun List<BlockPredicate>.toPaperBlockPredicate(): List<io.papermc.paper.block.BlockPredicate> {
            val blockPredicates = mutableListOf<io.papermc.paper.block.BlockPredicate>()

            this.forEach { blockPredicate ->
                blockPredicate.blocks
                    ?.map { TagKey.create(RegistryKey.BLOCK, it) }
                    ?.filter(Registry.BLOCK::hasTag)?.map(Registry.BLOCK::getTag)
                    ?.forEach { blockTag ->
                        blockPredicates += io.papermc.paper.block.BlockPredicate.predicate().blocks(blockTag).build()
                    }

                blockPredicate.blocks?.filter { Registry.BLOCK.get(it) != null }
                    ?.map { TypedKey.create(RegistryKey.BLOCK, it.key()) }
                    ?.let { blockKeys ->
                        val keySet = RegistrySet.keySet(RegistryKey.BLOCK, blockKeys)
                        blockPredicates += io.papermc.paper.block.BlockPredicate.predicate().blocks(keySet).build()
                    }
            }

            return blockPredicates
        }

        @Serializable
        data class BlockPredicate(
            val blocks: List<@Serializable(KeySerializer::class) Key>?
        ) {
            constructor(blockPredicate: io.papermc.paper.block.BlockPredicate) : this(
                blockPredicate.blocks()?.map { it.key() })
        }
    }

    @Serializable
    data class FoodProperties(
        val nutrition: Int,
        val saturation: Float,
        val eatSeconds: Float = 1.6f,
        val canAlwaysEat: Boolean = false,
        val effects: List<PossibleEffect> = emptyList(),
        val usingConvertsTo: SerializableItemStack? = null
    ) : DataType {

        constructor(foodProperties: io.papermc.paper.datacomponent.item.FoodProperties) : this(
            foodProperties.nutrition(),
            foodProperties.saturation(),
            foodProperties.eatSeconds(),
            foodProperties.canAlwaysEat(),
            foodProperties.effects().map(::PossibleEffect),
            foodProperties.usingConvertsTo()?.toSerializable()
        )

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.FOOD, io.papermc.paper.datacomponent.item.FoodProperties.food()
                    .nutrition(nutrition).saturation(saturation).eatSeconds(eatSeconds).canAlwaysEat(canAlwaysEat)
                    .addEffects(effects.map { it.paperPossibleEffect })
                    .usingConvertsTo(usingConvertsTo?.toItemStackOrNull())
            )
        }

        @Serializable
        data class PossibleEffect(
            val effect: @Serializable(PotionEffectSerializer::class) PotionEffect,
            val probability: Float = 1.0f
        ) {

            val paperPossibleEffect: io.papermc.paper.datacomponent.item.FoodProperties.PossibleEffect =
                io.papermc.paper.datacomponent.item.FoodProperties.PossibleEffect.of(effect, probability)

            constructor(possibleEffect: io.papermc.paper.datacomponent.item.FoodProperties.PossibleEffect) : this(
                possibleEffect.effect(),
                possibleEffect.probability()
            )
        }
    }

    @Serializable
    data class DyedColor(
        val color: @Serializable(ColorSerializer::class) Color,
        val showInToolTip: Boolean
    ) : DataType {
        constructor(dyedItemColor: DyedItemColor) : this(dyedItemColor.color(), dyedItemColor.showInTooltip())

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(color, showInToolTip))
        }
    }

    @Serializable
    @JvmInline
    value class MapColor(
        private val color: @Serializable(ColorSerializer::class) Color,
    ) : DataType {
        constructor(mapItemColor: MapItemColor) : this(mapItemColor.color())

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(DataComponentTypes.MAP_COLOR, MapItemColor.mapItemColor().color(color).build())
        }

    }

    @Serializable
    class Trim(
        val material: @Serializable(KeySerializer::class) Key,
        val pattern: @Serializable(KeySerializer::class) Key,
        val showInToolTip: Boolean = true
    ) : DataType {
        constructor(trim: ItemArmorTrim) : this(
            trim.armorTrim().material.key(),
            trim.armorTrim().pattern.key(),
            trim.showInTooltip()
        )

        override fun setDataType(itemStack: ItemStack) {
            val trimMaterial = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL).get(material)
                ?: error("Invalid TrimMaterial: " + material.asString())
            val trimPattern = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_PATTERN).get(pattern)
                ?: error("Invalid TrimPattern: " + pattern.asString())
            itemStack.setData(
                DataComponentTypes.TRIM,
                ItemArmorTrim.itemArmorTrim(ArmorTrim(trimMaterial, trimPattern), showInToolTip)
            )
        }

    }

    @Serializable
    data class JukeboxPlayable(
        val jukeboxSong: @Serializable(KeySerializer::class) Key,
        val showInToolTip: Boolean = true
    ) : DataType {
        constructor(jukeboxPlayable: io.papermc.paper.datacomponent.item.JukeboxPlayable) :
                this(jukeboxPlayable.jukeboxSong().key(), jukeboxPlayable.showInTooltip())

        override fun setDataType(itemStack: ItemStack) {
            val jukeboxRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.JUKEBOX_SONG)
            val jukeboxSong = jukeboxRegistry.get(jukeboxSong) ?: return
            itemStack.setData(
                DataComponentTypes.JUKEBOX_PLAYABLE,
                io.papermc.paper.datacomponent.item.JukeboxPlayable.jukeboxPlayable(jukeboxSong).showInTooltip(showInToolTip)
            )
        }
    }

    @Serializable
    data class AttributeModifiers(
        val attributes: List<SerializableAttribute>,
        val showInToolTip: Boolean = true
    ) : DataType {

        constructor(attributeModifiers: ItemAttributeModifiers) : this(
            attributeModifiers.modifiers().map(::SerializableAttribute), attributeModifiers.showInTooltip()
        )

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes().apply {
                attributes.forEach { addModifier(it.attribute, it.modifier) }
            }.showInTooltip(showInToolTip).build())
        }
    }


    @Serializable
    data class PotDecorations(
        val backItem: ItemType? = null,
        val frontItem: ItemType? = null,
        val leftItem: ItemType? = null,
        val rightItem: ItemType? = null,
    ) : DataType {
        constructor(potDecorations: io.papermc.paper.datacomponent.item.PotDecorations) :
                this(potDecorations.back(), potDecorations.front(), potDecorations.left(), potDecorations.right())

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(DataComponentTypes.POT_DECORATIONS, io.papermc.paper.datacomponent.item.PotDecorations.potDecorations(backItem, leftItem, rightItem, frontItem))
        }
    }


    @Serializable
    object FireResistant

    @Serializable
    object HideToolTip

    @Serializable
    object HideAdditionalTooltip

    @Serializable
    object CreativeSlotLock

    @Serializable
    object IntangibleProjectile

}
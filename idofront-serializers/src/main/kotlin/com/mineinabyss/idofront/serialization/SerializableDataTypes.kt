package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.serialization.SerializableDataTypes.ConsumeEffect.ClearAllEffectsConsumeEffect.toSerializable
import io.papermc.paper.datacomponent.DataComponentType
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.*
import io.papermc.paper.datacomponent.item.MapDecorations.DecorationEntry
import io.papermc.paper.datacomponent.item.Tool.Rule
import io.papermc.paper.datacomponent.item.consumable.*
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.TypedKey
import io.papermc.paper.registry.set.RegistrySet
import io.papermc.paper.registry.tag.TagKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.util.TriState
import org.bukkit.Color
import org.bukkit.Registry
import org.bukkit.Sound
import org.bukkit.entity.EntityType
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.trim.ArmorTrim
import org.bukkit.map.MapCursor
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType


@Suppress("UnstableApiUsage")
object SerializableDataTypes {

    fun <T : Any> setData(itemStack: ItemStack, dataComponent: DataComponentType.Valued<T>, any: T?) {
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
    class CustomModelData(val floats: List<Float> = listOf(), val flags: List<Boolean> = listOf(), val strings: List<String> = listOf(), val colors: List<@Serializable(ColorSerializer::class) Color> = listOf()) : DataType {
        constructor(customModelData: io.papermc.paper.datacomponent.item.CustomModelData) : this(customModelData.floats(), customModelData.flags(), customModelData.strings(), customModelData.colors())

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.CUSTOM_MODEL_DATA,
                io.papermc.paper.datacomponent.item.CustomModelData.customModelData()
                    .addFloats(floats).addFlags(flags).addStrings(strings).addColors(colors)
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
        val enchantments: List<SerializableEnchantment> = listOf(),
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
        constructor(entry: DecorationEntry) : this(entry.type(), entry.x(), entry.z(), entry.rotation())
        val paperDecoration: DecorationEntry = MapDecorations.decorationEntry(type, x, z, rotation)

        companion object {
            fun toPaperDecorations(decorations: List<MapDecoration>) =
                decorations.mapIndexed { i, mapDecoration -> i.toString() to mapDecoration.paperDecoration }.toMap()
        }
    }

    @Serializable
    @JvmInline
    value class Enchantable(val enchantable: Int) : DataType {
        constructor(enchantable: io.papermc.paper.datacomponent.item.Enchantable) : this(enchantable.value())

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(DataComponentTypes.ENCHANTABLE, io.papermc.paper.datacomponent.item.Enchantable.enchantable(enchantable))
        }
    }

    @Serializable
    @JvmInline
    value class Repairable(val repairable: List<@Serializable(KeySerializer::class) Key>) : DataType {
        constructor(repairable: io.papermc.paper.datacomponent.item.Repairable) :
                this(repairable.types().resolve(Registry.ITEM).map { it.key() })

        override fun setDataType(itemStack: ItemStack) {
            val items = repairable.mapNotNull { Registry.ITEM.get(it)?.key()?.let { TypedKey.create(RegistryKey.ITEM, it) } }
            val tags = repairable.mapNotNull {
                runCatching { Registry.ITEM.getTag(TagKey.create(RegistryKey.ITEM, it)) }.getOrNull()?.values()
            }.flatten()

            itemStack.setData(DataComponentTypes.REPAIRABLE, io.papermc.paper.datacomponent.item.Repairable.repairable(
                RegistrySet.keySet(RegistryKey.ITEM, items.plus(tags).filterNotNull().toMutableList())
            ))
        }
    }

    @Serializable
    data class Tool(
        val rules: List<Rule> = emptyList(),
        val defaultMiningSpeed: Float = 1f,
        val damagePerBlock: Int = 1,
        //val canDestroyBlocksInCreative: Boolean = true //1.21.5
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
            val correctForDrops: TriState = TriState.NOT_SET
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
                    rules += io.papermc.paper.datacomponent.item.Tool.rule(blockTag, rule.speed, rule.correctForDrops)
                }
                val blockKeys = rule.blockTypes.filter { Registry.BLOCK.get(it) != null }
                    .map { TypedKey.create(RegistryKey.BLOCK, it.key()) }
                val keySet = RegistrySet.keySet(RegistryKey.BLOCK, blockKeys)
                rules += io.papermc.paper.datacomponent.item.Tool.rule(keySet, rule.speed, rule.correctForDrops)
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
    sealed interface ConsumeEffect {

        fun io.papermc.paper.datacomponent.item.consumable.ConsumeEffect.toSerializable() = when (this) {
            is io.papermc.paper.datacomponent.item.consumable.ConsumeEffect.ApplyStatusEffects -> ApplyEffectsConsumeEffect(effects(), probability())
            is io.papermc.paper.datacomponent.item.consumable.ConsumeEffect.RemoveStatusEffects -> RemoveEffectsConsumeEffect(removeEffects().resolve(Registry.POTION_EFFECT_TYPE).toList())
            is io.papermc.paper.datacomponent.item.consumable.ConsumeEffect.TeleportRandomly -> TeleportConsumeEffect(diameter())
            is io.papermc.paper.datacomponent.item.consumable.ConsumeEffect.PlaySound -> PlaySoundConsumeEffect(sound())
            is io.papermc.paper.datacomponent.item.consumable.ConsumeEffect.ClearAllStatusEffects -> ClearAllEffectsConsumeEffect
            else -> ApplyEffectsConsumeEffect(emptyList(), 0f)
        }

        fun toPaperEffect() : io.papermc.paper.datacomponent.item.consumable.ConsumeEffect {
            return when (this) {
                is ApplyEffectsConsumeEffect -> io.papermc.paper.datacomponent.item.consumable.ConsumeEffect.applyStatusEffects(effects, probability)
                is RemoveEffectsConsumeEffect -> io.papermc.paper.datacomponent.item.consumable.ConsumeEffect.removeEffects(RegistrySet.keySetFromValues(RegistryKey.MOB_EFFECT, effects))
                is TeleportConsumeEffect -> io.papermc.paper.datacomponent.item.consumable.ConsumeEffect.teleportRandomlyEffect(diameter)
                is PlaySoundConsumeEffect -> io.papermc.paper.datacomponent.item.consumable.ConsumeEffect.playSoundConsumeEffect(key)
                is ClearAllEffectsConsumeEffect -> io.papermc.paper.datacomponent.item.consumable.ConsumeEffect.clearAllStatusEffects()
            }
        }

        @Serializable
        @SerialName("APPLY")
        data class ApplyEffectsConsumeEffect(
            val effects: List<@Serializable(PotionEffectSerializer::class) PotionEffect>,
            val probability: Float = 1.0f
        ): ConsumeEffect

        @Serializable
        @SerialName("REMOVE")
        data class RemoveEffectsConsumeEffect(val effects: List<@Serializable(PotionEffectTypeSerializer::class) PotionEffectType>): ConsumeEffect

        @Serializable
        @SerialName("TELEPORT")
        data class TeleportConsumeEffect(val diameter: Float = 16.0f): ConsumeEffect

        @Serializable
        @SerialName("SOUND")
        data class PlaySoundConsumeEffect(val key: @Serializable(KeySerializer::class) Key) : ConsumeEffect

        @Serializable
        @SerialName("CLEAR")
        object ClearAllEffectsConsumeEffect : ConsumeEffect, io.papermc.paper.datacomponent.item.consumable.ConsumeEffect.ClearAllStatusEffects
    }

    @Serializable
    @JvmInline
    value class UseRemainder(val useRemainder: SerializableItemStack) : DataType {

        init {
            require(useRemainder.toItemStackOrNull() != null) { "UseRemainder cannot be null"}
            require(!useRemainder.toItemStack().isEmpty) { "UseRemainder cannot be empty, was $useRemainder" }
        }

        constructor(useRemainder: io.papermc.paper.datacomponent.item.UseRemainder) : this(useRemainder.transformInto().toSerializable())

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.USE_REMAINDER,
                io.papermc.paper.datacomponent.item.UseRemainder.useRemainder(useRemainder.toItemStack())
            )
        }
    }

    @Serializable
    @JvmInline
    value class DamageResistant(val damageResistant: @Serializable(KeySerializer::class) Key) : DataType {
        constructor(damageResistant: io.papermc.paper.datacomponent.item.DamageResistant) : this(damageResistant.types().key())

        private val damageTypeRegistry get() = RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE)

        init {
            require(damageTypeRegistry.get(damageResistant) != null) { "${damageResistant.asString()} was not a valid DamageType"}
        }

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(DataComponentTypes.DAMAGE_RESISTANT, io.papermc.paper.datacomponent.item.DamageResistant.damageResistant(TagKey.create(RegistryKey.DAMAGE_TYPE, damageResistant)))
        }
    }

    @Serializable
    class DeathProtection(val deathEffects: List<ConsumeEffect> = listOf()) : DataType {
        constructor(deathProtection: io.papermc.paper.datacomponent.item.DeathProtection) : this(deathProtection.deathEffects().map { it.toSerializable() })

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(DataComponentTypes.DEATH_PROTECTION, io.papermc.paper.datacomponent.item.DeathProtection.deathProtection(deathEffects.map { it.toPaperEffect() }))
        }
    }

    @Serializable
    data class Consumable(
        val seconds: Float = 1.6f,
        val sound: @Serializable(KeySerializer::class) Key? = Sound.ENTITY_GENERIC_EAT.key(),
        val animation: ItemUseAnimation = ItemUseAnimation.EAT,
        val particles: Boolean = true,
        val consumeEffects: List<ConsumeEffect> = listOf()
    ) : DataType {
        constructor(consumable: io.papermc.paper.datacomponent.item.Consumable) : this(
            consumable.consumeSeconds(),
            consumable.sound(),
            consumable.animation(),
            consumable.hasConsumeParticles(),
            consumable.consumeEffects().map { it.toSerializable() }
        )

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.CONSUMABLE, io.papermc.paper.datacomponent.item.Consumable.consumable()
                    .consumeSeconds(seconds)
                    .run { if(sound != null) sound(sound) else this }
                    .animation(animation)
                    .hasConsumeParticles(particles)
                    .addEffects(consumeEffects.map { it.toPaperEffect() })
            )
        }
    }

    @Serializable
    data class FoodProperties(
        val nutrition: Int,
        val saturation: Float,
        val canAlwaysEat: Boolean = false
    ) : DataType {

        init {
            require(nutrition >= 0) { "Nutrition must be a non-negative integer, was $nutrition" }
        }

        constructor(foodProperties: io.papermc.paper.datacomponent.item.FoodProperties) : this(
            foodProperties.nutrition(),
            foodProperties.saturation(),
            foodProperties.canAlwaysEat()
        )

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.FOOD, io.papermc.paper.datacomponent.item.FoodProperties.food()
                    .nutrition(nutrition).saturation(saturation).canAlwaysEat(canAlwaysEat)
            )
        }
    }

    @Serializable
    data class DyedColor(
        val color: @Serializable(ColorSerializer::class) Color,
        val showInToolTip: Boolean = true
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
    data class Equippable(
        val slot: EquipmentSlot,
        val model: @Serializable(KeySerializer::class) Key? = null,
        val cameraOverlay: @Serializable(KeySerializer::class) Key? = null,
        val equipSound: @Serializable(KeySerializer::class) Key? = Sound.ITEM_ARMOR_EQUIP_GENERIC.key(),
        val allowedEntities: List<EntityType>? = null,
        val damageOnHurt: Boolean = true,
        val swappable: Boolean = true,
        val dispensable: Boolean = true,

    ) : DataType {
        constructor(equippable: io.papermc.paper.datacomponent.item.Equippable) : this(
            equippable.slot(), equippable.assetId(), equippable.cameraOverlay(), equippable.equipSound(),
            equippable.allowedEntities()?.resolve(Registry.ENTITY_TYPE)?.toList(),
            equippable.damageOnHurt(), equippable.swappable(), equippable.dispensable()
        )

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.EQUIPPABLE,
                io.papermc.paper.datacomponent.item.Equippable.equippable(slot).assetId(model).cameraOverlay(cameraOverlay)
                    .run { if(equipSound != null) equipSound(equipSound) else this }
                    .allowedEntities(allowedEntities?.let { RegistrySet.keySetFromValues(RegistryKey.ENTITY_TYPE, it) })
                    .damageOnHurt(damageOnHurt).swappable(swappable).dispensable(dispensable)
            )
        }
    }

    @Serializable
    class Trim(
        val material: @Serializable(KeySerializer::class) Key,
        val pattern: @Serializable(KeySerializer::class) Key,
        val showInToolTip: Boolean = true
    ) : DataType {
        constructor(trim: ItemArmorTrim) : this(
            RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL).getKey(trim.armorTrim().material)!!,
            RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_PATTERN).getKey(trim.armorTrim().pattern)!!,
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
    data class UseCooldown(
        val seconds: Float,
        val group: @Serializable(KeySerializer::class) Key? = null
    ) : DataType {

        init {
            require(seconds <= 0) { "Seconds cannot be below 0" }
        }

        constructor(cooldown: io.papermc.paper.datacomponent.item.UseCooldown) : this(cooldown.seconds(), cooldown.cooldownGroup())

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(DataComponentTypes.USE_COOLDOWN, io.papermc.paper.datacomponent.item.UseCooldown.useCooldown(seconds).cooldownGroup(group).build())
        }
    }



    @Serializable
    data class PotDecorations(
        val backItem: @Serializable(KeySerializer::class) Key? = null,
        val frontItem: @Serializable(KeySerializer::class) Key? = null,
        val leftItem: @Serializable(KeySerializer::class) Key? = null,
        val rightItem: @Serializable(KeySerializer::class) Key? = null,
    ) : DataType {
        constructor(potDecorations: io.papermc.paper.datacomponent.item.PotDecorations) :
                this(potDecorations.back()?.key(), potDecorations.front()?.key(), potDecorations.left()?.key(), potDecorations.right()?.key())

        override fun setDataType(itemStack: ItemStack) {
            val (back, front) = backItem?.let { Registry.ITEM.get(it) } to frontItem?.let { Registry.ITEM.get(it) }
            val (left, right) = leftItem?.let { Registry.ITEM.get(it) } to rightItem?.let { Registry.ITEM.get(it) }
            itemStack.setData(DataComponentTypes.POT_DECORATIONS, io.papermc.paper.datacomponent.item.PotDecorations.potDecorations(back, left, right, front))
        }
    }

    @Serializable
    @JvmInline
    value class MapId(val mapId: Int) : DataType {
        constructor(mapId: io.papermc.paper.datacomponent.item.MapId) : this(mapId.id())

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(DataComponentTypes.MAP_ID, io.papermc.paper.datacomponent.item.MapId.mapId(mapId))
        }
    }

    @Serializable
    object HideToolTip

    @Serializable
    object HideAdditionalTooltip

    @Serializable
    object IntangibleProjectile

    @Serializable
    object Glider

}

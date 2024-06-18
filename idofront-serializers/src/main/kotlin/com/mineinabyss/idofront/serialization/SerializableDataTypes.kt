package com.mineinabyss.idofront.serialization

import io.papermc.paper.block.BlockPredicate
import io.papermc.paper.component.DataComponentType
import io.papermc.paper.component.DataComponentTypes
import io.papermc.paper.component.item.*
import io.papermc.paper.component.item.Tool.Rule
import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.TypedKey
import io.papermc.paper.registry.set.RegistrySet
import io.papermc.paper.registry.tag.TagKey
import kotlinx.serialization.Serializable
import net.kyori.adventure.key.Key
import net.kyori.adventure.util.TriState
import org.bukkit.Color
import org.bukkit.Registry
import org.bukkit.inventory.ItemStack
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
        constructor(unbreakable: io.papermc.paper.component.item.Unbreakable) : this(unbreakable.showInTooltip())

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.UNBREAKABLE,
                io.papermc.paper.component.item.Unbreakable.unbreakable(shownInTooltip)
            )
        }
    }

    @Serializable
    data class PotionContents(
        val potionType: @Serializable(PotionTypeSerializer::class) org.bukkit.potion.PotionType?,
        val color: @Serializable(ColorSerializer::class) Color?,
        val customEffects: List<@Serializable(PotionEffectSerializer::class) PotionEffect> = emptyList()
    ) : DataType {

        constructor(potionContents: io.papermc.paper.component.item.PotionContents) : this(
            potionContents.potion(),
            potionContents.customColor(),
            potionContents.customEffects()
        )

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.POTION_CONTENTS,
                io.papermc.paper.component.item.PotionContents.potionContents().potion(potionType).customColor(color)
                    .addAll(customEffects).build()
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
    data class Tool(
        val rules: List<Rule> = emptyList(),
        val defaultMiningSpeed: Float,
        val damagePerBlock: Int
    ) : BlockTags(), DataType {
        constructor(tool: io.papermc.paper.component.item.Tool) : this(
            tool.rules().map(::Rule),
            tool.defaultMiningSpeed(),
            tool.damagePerBlock()
        )

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.TOOL, io.papermc.paper.component.item.Tool.tool()
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
            constructor(rule: io.papermc.paper.component.item.Tool.Rule) : this(
                rule.blockTypes().map { it.key() },
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
        constructor(adventurePredicate: ItemAdventurePredicate) :
                this(adventurePredicate.showInTooltip(), adventurePredicate.modifiers().map(::BlockPredicate))

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
        constructor(adventurePredicate: ItemAdventurePredicate) :
                this(adventurePredicate.showInTooltip(), adventurePredicate.modifiers().map(::BlockPredicate))

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

        constructor(foodProperties: io.papermc.paper.component.item.FoodProperties) : this(
            foodProperties.nutrition(),
            foodProperties.saturation(),
            foodProperties.eatSeconds(),
            foodProperties.canAlwaysEat(),
            foodProperties.effects().map(::PossibleEffect),
            foodProperties.usingConvertsTo()?.toSerializable()
        )

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.FOOD, io.papermc.paper.component.item.FoodProperties.food()
                    .nutrition(nutrition).saturation(saturation).eatSeconds(eatSeconds).canAlwaysEat(canAlwaysEat)
                    .addAllEffects(effects.map { it.paperPossibleEffect })
                    .usingConvertsTo(usingConvertsTo?.toItemStackOrNull())
            )
        }

        @Serializable
        data class PossibleEffect(
            val effect: @Serializable(PotionEffectSerializer::class) PotionEffect,
            val probability: Float = 1.0f
        ) {

            val paperPossibleEffect: io.papermc.paper.component.item.FoodProperties.PossibleEffect =
                io.papermc.paper.component.item.FoodProperties.PossibleEffect.of(effect, probability)

            constructor(possibleEffect: io.papermc.paper.component.item.FoodProperties.PossibleEffect) : this(
                possibleEffect.effect(),
                possibleEffect.probability()
            )
        }
    }

    @Serializable
    @JvmInline
    value class CustomModelData(private val customModelData: Int) : DataType {
        constructor(customModelData: io.papermc.paper.component.item.CustomModelData) : this(customModelData.data())

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.CUSTOM_MODEL_DATA,
                io.papermc.paper.component.item.CustomModelData.customModelData().customModelData(customModelData)
                    .build()
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
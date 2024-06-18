package com.mineinabyss.idofront.serialization

import io.papermc.paper.component.DataComponentType
import io.papermc.paper.component.DataComponentTypes
import io.papermc.paper.component.item.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Color
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.potion.PotionEffect

object SerializableDataTypes {

    fun <T> setData(itemStack: ItemStack, dataComponent: DataComponentType.Valued<T>, any: T?) {
        any?.let { itemStack.setData(dataComponent, any) }
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
        constructor(itemEnchantments: ItemEnchantments) : this(itemEnchantments.enchantments().map(::SerializableEnchantment), itemEnchantments.showInTooltip())
        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.ENCHANTMENTS,
                ItemEnchantments.itemEnchantments(enchantments.associate { it.enchant to it.level }, showInToolTip)
            )
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

        constructor(attributeModifiers: ItemAttributeModifiers) : this(attributeModifiers.modifiers().map(::SerializableAttribute), attributeModifiers.showInTooltip())
        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes().apply {
                attributes.forEach { addModifier(it.attribute, it.modifier) }
            }.showInTooltip(showInToolTip).build())
        }
    }

    @Serializable(with = FireResistantSerializer::class)
    object FireResistant : UnvaluedDataType() {
        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(DataComponentTypes.FIRE_RESISTANT)
        }
    }

    @Serializable(with = HideToolTipSerializer::class)
    object HideToolTip : UnvaluedDataType() {
        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(DataComponentTypes.HIDE_TOOLTIP)
        }
    }

    @Serializable(with = HideAdditionalTooltipSerializer::class)
    object HideAdditionalTooltip : UnvaluedDataType() {
        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP)
        }
    }

    @Serializable(with = CreativeSlotLockSerializer::class)
    object CreativeSlotLock : UnvaluedDataType() {
        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(DataComponentTypes.CREATIVE_SLOT_LOCK)
        }
    }

    @Serializable(with = IntangibleProjectileSerializer::class)
    object IntangibleProjectile : UnvaluedDataType() {
        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(DataComponentTypes.INTANGIBLE_PROJECTILE)
        }
    }

    object FireResistantSerializer : KSerializer<FireResistant> by UnvaluedDataTypeSerializer("fireResistant")
    object HideToolTipSerializer : KSerializer<HideToolTip> by UnvaluedDataTypeSerializer("hideTooltip")
    object HideAdditionalTooltipSerializer :
        KSerializer<HideAdditionalTooltip> by UnvaluedDataTypeSerializer("hideAdditionalTooltip")

    object CreativeSlotLockSerializer : KSerializer<CreativeSlotLock> by UnvaluedDataTypeSerializer("creativeSlotLock")
    object IntangibleProjectileSerializer :
        KSerializer<IntangibleProjectile> by UnvaluedDataTypeSerializer("intangibleProjectile")

    abstract class UnvaluedDataType : DataType

    class UnvaluedDataTypeSerializer<T : UnvaluedDataType>(private val serialName: String) : KSerializer<T> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(serialName, PrimitiveKind.BOOLEAN)

        override fun serialize(encoder: Encoder, value: T) {
            encoder.encodeBoolean(true)
        }

        override fun deserialize(decoder: Decoder): T {
            decoder.decodeBoolean() // just to match the encoding process
            throw UnsupportedOperationException("Deserialization of $serialName is not supported")
        }
    }


}
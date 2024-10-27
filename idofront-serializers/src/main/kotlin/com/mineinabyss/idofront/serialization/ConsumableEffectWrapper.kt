package com.mineinabyss.idofront.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.components.FoodComponent
import org.bukkit.potion.PotionEffect

/*
@Serializable
class ConsumableEffectWrapper(
    val effect: @Serializable(PotionEffectSerializer::class) PotionEffect,
    val probability: Float = 1.0f
)

object ConsumableComponentSerializer : KSerializer<ConsumableComponent> {
    override val descriptor: SerialDescriptor = ConsumableEffectWrapper.serializer().descriptor
    override fun serialize(encoder: Encoder, value: FoodComponent) {
        val surrogate = ConsumableEffectWrapper(
            value.nutrition,
            value.saturation,
            value.canAlwaysEat(),
        )
        encoder.encodeSerializableValue(ConsumableEffectWrapper.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): ConsumableComponent {
        return ItemStack(Material.PAPER).itemMeta.consumable.apply {
            val surrogate = decoder.decodeSerializableValue(ConsumableEffectWrapper.serializer())

        }
    }
}*/

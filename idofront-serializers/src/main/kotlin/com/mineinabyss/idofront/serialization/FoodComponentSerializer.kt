package com.mineinabyss.idofront.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.components.FoodComponent

@Serializable
@SerialName("FoodComponent")
private class FoodComponentSurrogate(
    val nutrition: Int,
    val saturation: Float,
    val canAlwaysEat: Boolean = false,
) {
    init {
        require(nutrition >= 0) { "FoodComponent must have a positive nutrition" }
        require(saturation >= 0) { "FoodComponent must have a positive saturation" }
    }
}

object FoodComponentSerializer : KSerializer<FoodComponent> {
    override val descriptor: SerialDescriptor = FoodComponentSurrogate.serializer().descriptor
    override fun serialize(encoder: Encoder, value: FoodComponent) {
        val surrogate = FoodComponentSurrogate(
            value.nutrition,
            value.saturation,
            value.canAlwaysEat(),
        )
        encoder.encodeSerializableValue(FoodComponentSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): FoodComponent {
        return ItemStack(Material.PAPER).itemMeta.food.apply {
            val surrogate = decoder.decodeSerializableValue(FoodComponentSurrogate.serializer())
            nutrition = surrogate.nutrition
            saturation = surrogate.saturation
            setCanAlwaysEat(surrogate.canAlwaysEat)
        }
    }
}
package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.time.inWholeTicks
import com.mineinabyss.idofront.time.ticks
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.components.FoodComponent
import org.bukkit.inventory.meta.components.FoodComponent.FoodEffect
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

@Serializable
@SerialName("FoodComponent")
private class FoodComponentSurrogate(
    val nutrition: Int,
    val saturation: Float,
    val eatSeconds: Float = 1.6f,
    val canAlwaysEat: Boolean = false,
    val usingConvertsTo: SerializableItemStack? = null,
    val effects: List<FoodEffectWrapper> = emptyList()
) {
    init {
        require(nutrition >= 0) { "FoodComponent must have a positive nutrition" }
        require(saturation >= 0) { "FoodComponent must have a positive saturation" }
        require(eatSeconds >= 0) { "FoodComponent must have a positive eatSeconds" }
        require(effects.all { it.probability in 0.0..1.0 }) { "FoodEffect-probability must be between 0.0..1.0" }
    }
}

object FoodComponentSerializer : KSerializer<FoodComponent> {
    override val descriptor: SerialDescriptor = FoodComponentSurrogate.serializer().descriptor
    override fun serialize(encoder: Encoder, value: FoodComponent) {
        val surrogate = FoodComponentSurrogate(
            value.nutrition,
            value.saturation,
            value.eatSeconds,
            value.canAlwaysEat(),
            value.usingConvertsTo?.toSerializable(),
            value.effects.map { FoodEffectWrapper(it.effect, it.probability) }
        )
        encoder.encodeSerializableValue(FoodComponentSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): FoodComponent {
        return ItemStack(Material.PAPER).itemMeta.food.apply {
            val surrogate = decoder.decodeSerializableValue(FoodComponentSurrogate.serializer())
            nutrition = surrogate.nutrition
            saturation = surrogate.saturation
            setCanAlwaysEat(surrogate.canAlwaysEat)
            eatSeconds = surrogate.eatSeconds
            usingConvertsTo = surrogate.usingConvertsTo?.toItemStackOrNull()
            surrogate.effects.forEach { addEffect(it.effect, it.probability) }
        }
    }
}
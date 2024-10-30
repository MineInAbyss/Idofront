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
class FoodComponentSurrogate(
    val nutrition: Int,
    val saturation: Float,
    val eatSeconds: Float = 1.6f,
    val canAlwaysEat: Boolean = false,
    val usingConvertsTo: SerializableItemStack? = null,
    val effects: List<FoodEffectWrapper> = emptyList()
) {

    constructor(food: FoodComponent) : this(
        food.nutrition,
        food.saturation,
        food.eatSeconds,
        food.canAlwaysEat(),
        food.usingConvertsTo?.toSerializable(),
        food.effects.map { FoodEffectWrapper(it.effect, it.probability) })

    init {
        require(nutrition >= 0) { "FoodComponent must have a positive nutrition" }
        require(saturation >= 0) { "FoodComponent must have a positive saturation" }
        require(eatSeconds >= 0) { "FoodComponent must have a positive eatSeconds" }
        require(effects.all { it.probability in 0.0..1.0 }) { "FoodEffect-probability must be between 0.0..1.0" }
    }

    val foodComponent: FoodComponent
        get() = ItemStack.of(Material.PAPER).itemMeta.food.also {
            it.nutrition = nutrition
            it.saturation = saturation
            it.eatSeconds = eatSeconds
            it.setCanAlwaysEat(canAlwaysEat)
            it.usingConvertsTo = usingConvertsTo?.toItemStackOrNull()
            effects.forEach { e -> it.addEffect(e.effect, e.probability) }
        }
}

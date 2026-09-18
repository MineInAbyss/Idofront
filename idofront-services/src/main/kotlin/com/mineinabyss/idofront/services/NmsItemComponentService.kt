package com.mineinabyss.idofront.services

import net.kyori.adventure.key.Key
import org.bukkit.inventory.ItemStack

/**
 * A number the client resolves per use, either a fixed [Constant]
 * or the key of an entry in the matching context provider registry
 */
sealed interface Resolvable {
    data class Constant(val value: Double) : Resolvable
    data class Provider(val key: Key) : Resolvable
}

data class Compostable(val layers: Resolvable)
data class CookingFuel(val burnTime: Resolvable, val speedMultiplier: Resolvable)
data class BrewingFuel(val uses: Resolvable, val speedMultiplier: Resolvable)

/**
 * 26.3 added compostable, cooking_fuel and brewing_fuel without a Paper API,
 * so they are only reachable through NMS. Idofront registers the implementation itself.
 *
 * ## Example usage
 *
 * ```kotlin
 * Services.get<NmsItemComponentService>()?.setCompostable(item, Compostable(Resolvable.Constant(1.0)))
 * ```
 */
interface NmsItemComponentService {
    fun getCompostable(itemStack: ItemStack): Compostable?
    fun setCompostable(itemStack: ItemStack, compostable: Compostable?)

    fun getCookingFuel(itemStack: ItemStack): CookingFuel?
    fun setCookingFuel(itemStack: ItemStack, cookingFuel: CookingFuel?)

    fun getBrewingFuel(itemStack: ItemStack): BrewingFuel?
    fun setBrewingFuel(itemStack: ItemStack, brewingFuel: BrewingFuel?)
}

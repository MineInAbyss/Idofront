package com.mineinabyss.idofront.nms.components

import com.mineinabyss.idofront.services.BrewingFuel
import com.mineinabyss.idofront.services.Compostable
import com.mineinabyss.idofront.services.CookingFuel
import com.mineinabyss.idofront.services.NmsItemComponentService
import com.mineinabyss.idofront.services.Resolvable
import net.kyori.adventure.key.Key
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.Identifier
import net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt
import org.bukkit.craftbukkit.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import net.minecraft.world.item.component.BrewingFuel as NMSBrewingFuel
import net.minecraft.world.item.component.Compostable as NMSCompostable
import net.minecraft.world.item.component.CookingFuel as NMSCookingFuel

class NmsItemComponentServiceImpl : NmsItemComponentService {
    override fun getCompostable(itemStack: ItemStack): Compostable? =
        itemStack.components.get(DataComponents.COMPOSTABLE)?.let { Compostable(it.layers().toResolvable()) }

    override fun setCompostable(itemStack: ItemStack, compostable: Compostable?) {
        itemStack.setComponent(DataComponents.COMPOSTABLE, compostable?.let { NMSCompostable(it.layers.toResolvableInt()) })
    }

    override fun getCookingFuel(itemStack: ItemStack): CookingFuel? =
        itemStack.components.get(DataComponents.COOKING_FUEL)
            ?.let { CookingFuel(it.burnTime().toResolvable(), it.speedMultiplier().toResolvable()) }

    override fun setCookingFuel(itemStack: ItemStack, cookingFuel: CookingFuel?) {
        itemStack.setComponent(
            DataComponents.COOKING_FUEL,
            cookingFuel?.let { NMSCookingFuel(it.burnTime.toResolvableInt(), it.speedMultiplier.toResolvableFloat()) }
        )
    }

    override fun getBrewingFuel(itemStack: ItemStack): BrewingFuel? =
        itemStack.components.get(DataComponents.BREWING_FUEL)
            ?.let { BrewingFuel(it.uses().toResolvable(), it.speedMultiplier().toResolvable()) }

    override fun setBrewingFuel(itemStack: ItemStack, brewingFuel: BrewingFuel?) {
        itemStack.setComponent(
            DataComponents.BREWING_FUEL,
            brewingFuel?.let { NMSBrewingFuel(it.uses.toResolvableInt(), it.speedMultiplier.toResolvableFloat()) }
        )
    }
}

/** Mutates the backing NMS stack in place, so the caller's [ItemStack] reflects the change */
private val ItemStack.components get() = CraftItemStack.getCraftStack(this).handle.components

private fun <T : Any> ItemStack.setComponent(type: net.minecraft.core.component.DataComponentType<T>, value: T?) {
    val handle = CraftItemStack.getCraftStack(this).handle
    if (value == null) handle.remove(type) else handle.set(type, value)
}

private fun ResolvableInt.toResolvable(): Resolvable = when (this) {
    is ResolvableInt.Constant -> Resolvable.Constant(value().toDouble())
    is ResolvableInt.Reference -> Resolvable.Provider(Key.key(key().identifier().toString()))
}

private fun ResolvableFloat.toResolvable(): Resolvable = when (this) {
    is ResolvableFloat.Constant -> Resolvable.Constant(value().toDouble())
    is ResolvableFloat.Reference -> Resolvable.Provider(Key.key(key().identifier().toString()))
}

private fun Resolvable.toResolvableInt(): ResolvableInt = when (this) {
    is Resolvable.Constant -> ResolvableInt.Constant(value.toInt())
    is Resolvable.Provider -> ResolvableInt.fromKey(ResourceKey.create(Registries.CONTEXT_INT_PROVIDER, key.toIdentifier()))
}

private fun Resolvable.toResolvableFloat(): ResolvableFloat = when (this) {
    is Resolvable.Constant -> ResolvableFloat.Constant(value.toFloat())
    is Resolvable.Provider -> ResolvableFloat.fromKey(ResourceKey.create(Registries.CONTEXT_FLOAT_PROVIDER, key.toIdentifier()))
}

private fun Key.toIdentifier() = Identifier.fromNamespaceAndPath(namespace(), value())

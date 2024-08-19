package com.mineinabyss.idofront.serialization.recipes.options

import org.bukkit.inventory.ItemStack

internal data class IngredientInfo(
    val item: ItemStack,
    val slot: Int,
    val options: List<IngredientOption>,
)

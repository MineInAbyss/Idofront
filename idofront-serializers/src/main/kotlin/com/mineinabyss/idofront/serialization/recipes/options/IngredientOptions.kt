package com.mineinabyss.idofront.serialization.recipes.options

import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice

data class IngredientOptions(
    val options: Map<RecipeChoice, List<IngredientOption>> = mapOf(),
) {
    fun getOptionsFor(item: ItemStack): List<IngredientOption> {
        val choice = options.entries.firstOrNull { it.key.test(item) } ?: return emptyList()
        return choice.value
    }
}

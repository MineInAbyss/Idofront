package com.mineinabyss.idofront.serialization.recipes.options

import org.bukkit.inventory.Recipe

data class RecipeWithOptions(
    val recipe: Recipe,
    val options: IngredientOptions,
)

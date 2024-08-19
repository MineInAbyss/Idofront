package com.mineinabyss.idofront.serialization.recipes

import com.mineinabyss.idofront.recipes.register
import com.mineinabyss.idofront.serialization.recipes.options.IngredientOptions
import com.mineinabyss.idofront.serialization.recipes.options.RecipeWithOptions
import com.mineinabyss.idofront.serialization.recipes.options.ingredientOptionsListener
import kotlinx.serialization.Serializable
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe

@Serializable
sealed class SerializableRecipeIngredients {
    abstract fun toRecipe(key: NamespacedKey, result: ItemStack, group: String = "", category: String = "MISC"): Recipe

    open fun toRecipeWithOptions(
        key: NamespacedKey,
        result: ItemStack,
        group: String = "",
        category: String = "MISC",
    ): RecipeWithOptions {
        val recipe = toRecipe(key, result, group, category)
        return RecipeWithOptions(recipe, IngredientOptions())
    }

    fun registerRecipeWithOptions(
        key: NamespacedKey,
        result: ItemStack,
        group: String = "",
        category: String = "MISC",
    ) {
        val (recipe, options) = toRecipeWithOptions(key, result, group, category)
        recipe.register()
        ingredientOptionsListener.keyToOptions[key.asString()] = options
    }
}

package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.util.toMCKey
import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.ShapedRecipe

@Serializable
class SerializableRecipeIngredients(
    val items: Map<String, SerializableItemStack>,
    val configuration: String,
) {
    fun toCraftingRecipe(key: NamespacedKey, result: ItemStack): Recipe {
        val recipe = ShapedRecipe(key, result)

        recipe.shape(*configuration.replace("|", "").split("\n").toTypedArray())

        items.forEach { (key, ingredient) ->
            recipe.setIngredient(key[0], RecipeChoice.ExactChoice(ingredient.toItemStack()))
        }

        return recipe
    }

    fun register(key: NamespacedKey, result: ItemStack) {
        Bukkit.getServer().addRecipe(toCraftingRecipe(key, result))
    }
}

@Serializable
class SerializableRecipe(
    val key: String,
    val ingredients: SerializableRecipeIngredients,
    val result: SerializableItemStack,
) {
    fun toCraftingRecipe() =
        ingredients.toCraftingRecipe(key.toMCKey(), result.toItemStack())

    fun register() {
        Bukkit.getServer().addRecipe(toCraftingRecipe())
    }
}

package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.util.toMCKey
import kotlinx.serialization.Serializable
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.ShapedRecipe

@Serializable
open class SerializableRecipeIngredients(
    val items: Map<String, SerializableItemStack>,
    val configuration: String,
) {
    fun toCraftingRecipe(key: NamespacedKey, result: ItemStack, group: String = ""): Recipe {
        val recipe = ShapedRecipe(key, result)

        recipe.shape(*configuration.replace("|", "").split("\n").toTypedArray())

        recipe.group = group

        items.forEach { (key, ingredient) ->
            recipe.setIngredient(key[0], RecipeChoice.ExactChoice(ingredient.toItemStack()))
        }

        return recipe
    }
}

@Serializable
class SerializableRecipe(
    val key: String,
    val ingredients: SerializableRecipeIngredients,
    val result: SerializableItemStack,
    val group: String = "",
) {
    fun toCraftingRecipe() =
        ingredients.toCraftingRecipe(key.toMCKey(), result.toItemStack(), group)
}

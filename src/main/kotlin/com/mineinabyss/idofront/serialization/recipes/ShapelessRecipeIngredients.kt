package com.mineinabyss.idofront.serialization.recipes

import com.mineinabyss.idofront.serialization.SerializableItemStack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.ShapelessRecipe

@Serializable
@SerialName("shapeless")
class ShapelessRecipeIngredients(
    val items: List<SerializableItemStack>,
) : SerializableRecipeIngredients() {
    override fun toRecipe(key: NamespacedKey, result: ItemStack, group: String): Recipe {
        val recipe = ShapelessRecipe(key, result)

        recipe.group = group

        items.forEach { ingredient ->
            recipe.addIngredient(RecipeChoice.ExactChoice(ingredient.toItemStack()))
        }

        return recipe
    }
}
package com.mineinabyss.idofront.serialization.recipes

import com.mineinabyss.idofront.serialization.SerializableItemStack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.NamespacedKey
import org.bukkit.inventory.CampfireRecipe
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.recipe.CookingBookCategory

@Serializable
@SerialName("campfire")
class CampfireRecipeIngredients(
    val input: SerializableItemStack,
    val experience: Float,
    val cookingTime: Int,
) : SerializableRecipeIngredients() {
    override fun toRecipe(key: NamespacedKey, result: ItemStack, group: String, category: String): Recipe {
        val recipe = CampfireRecipe(key, result, RecipeChoice.ExactChoice(input.toItemStack()), experience, cookingTime)

        recipe.group = group
        recipe.category = CookingBookCategory.entries.find { it.name == category } ?: CookingBookCategory.MISC

        return recipe
    }
}
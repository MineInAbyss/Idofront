package com.mineinabyss.idofront.serialization.recipes

import com.mineinabyss.idofront.serialization.SerializableItemStack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.NamespacedKey
import org.bukkit.inventory.BlastingRecipe
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.RecipeChoice

@Serializable
@SerialName("blasting")
class BlastingRecipeIngredients(
    val input: SerializableItemStack,
    val experience: Float,
    val cookingTime: Int
) : SerializableRecipeIngredients() {
    override fun toRecipe(key: NamespacedKey, result: ItemStack, group: String): Recipe {
        val recipe = BlastingRecipe(key, result, RecipeChoice.ExactChoice(input.toItemStack()), experience, cookingTime)

        recipe.group = group

        return recipe
    }
}
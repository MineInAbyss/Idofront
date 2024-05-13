package com.mineinabyss.idofront.serialization.recipes

import com.mineinabyss.idofront.serialization.SerializableItemStack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.StonecuttingRecipe

@Serializable
@SerialName("stonecutting")
class StonecuttingRecipeIngredients(
    val input: SerializableItemStack,
) : SerializableRecipeIngredients() {
    override fun toRecipe(key: NamespacedKey, result: ItemStack, group: String, category: String): Recipe {
        val recipe = StonecuttingRecipe(key, result, RecipeChoice.ExactChoice(input.toItemStack()))

        recipe.group = group

        return recipe
    }
}
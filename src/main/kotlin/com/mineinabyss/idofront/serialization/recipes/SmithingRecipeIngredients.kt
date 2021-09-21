package com.mineinabyss.idofront.serialization.recipes

import com.mineinabyss.idofront.serialization.SerializableItemStack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.SmithingRecipe

@Serializable
@SerialName("smithing")
class SmithingRecipeIngredients(
    val input: SerializableItemStack,
    val addition: SerializableItemStack,
) : SerializableRecipeIngredients() {
    override fun toRecipe(key: NamespacedKey, result: ItemStack, group: String): Recipe {
        return SmithingRecipe(
            key,
            result,
            RecipeChoice.ExactChoice(input.toItemStack()),
            RecipeChoice.ExactChoice(addition.toItemStack())
        )
    }
}
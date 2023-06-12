package com.mineinabyss.idofront.serialization.recipes

import com.mineinabyss.idofront.serialization.SerializableItemStack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.SmithingTransformRecipe

@Serializable
@SerialName("smithing_transform")
class SmithingTransformRecipeIngredients(
    val input: SerializableItemStack,
    val template: SerializableItemStack = SerializableItemStack(Material.AIR),
    val addition: SerializableItemStack,
    val copyNbt: Boolean = false
) : SerializableRecipeIngredients() {
    override fun toRecipe(key: NamespacedKey, result: ItemStack, group: String): Recipe {
        return SmithingTransformRecipe(
            key,
            result,
            RecipeChoice.ExactChoice(template.toItemStack()),
            RecipeChoice.ExactChoice(input.toItemStack()),
            RecipeChoice.ExactChoice(addition.toItemStack()),
            copyNbt
        )
    }
}

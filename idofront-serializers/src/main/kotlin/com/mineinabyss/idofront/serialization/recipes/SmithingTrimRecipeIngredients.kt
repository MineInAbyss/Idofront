package com.mineinabyss.idofront.serialization.recipes

import com.mineinabyss.idofront.serialization.SerializableItemStack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.SmithingTrimRecipe
import org.bukkit.inventory.meta.trim.TrimPattern

@Serializable
@SerialName("smithing_trim")
class SmithingTrimRecipeIngredients(
    val input: SerializableItemStack,
    val template: SerializableItemStack = SerializableItemStack(Material.AIR),
    val addition: SerializableItemStack,
    val copyNbt: Boolean = false
) : SerializableRecipeIngredients() {
    override fun toRecipe(key: NamespacedKey, result: ItemStack, group: String, category: String): Recipe {
        val (template, input) = template.toRecipeChoice() to input.toRecipeChoice()
        val (addition, pattern) = addition.toRecipeChoice() to TrimPattern.BOLT

        return SmithingTrimRecipe(key, template, input, addition, pattern, copyNbt)
    }
}

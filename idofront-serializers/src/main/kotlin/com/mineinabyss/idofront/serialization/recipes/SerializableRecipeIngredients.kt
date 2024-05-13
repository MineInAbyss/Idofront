package com.mineinabyss.idofront.serialization.recipes

import kotlinx.serialization.Serializable
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe

@Serializable
sealed class SerializableRecipeIngredients {
    abstract fun toRecipe(key: NamespacedKey, result: ItemStack, group: String = "", category: String = "MISC"): Recipe
}
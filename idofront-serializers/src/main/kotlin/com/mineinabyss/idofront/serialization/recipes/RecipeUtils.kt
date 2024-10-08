package com.mineinabyss.idofront.serialization.recipes

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Tag
import org.bukkit.inventory.RecipeChoice

object RecipeUtils {
    fun getMaterialChoiceForTag(key: NamespacedKey) = RecipeChoice.MaterialChoice(
        Bukkit.getTag(
            Tag.REGISTRY_BLOCKS,
            key,
            Material::class.java
        ) ?: Bukkit.getTag(
            Tag.REGISTRY_ITEMS,
            key,
            Material::class.java
        ) ?: Tag.DIRT
    )
}

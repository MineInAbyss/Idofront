package com.mineinabyss.idofront.serialization.recipes

import com.mineinabyss.idofront.serialization.SerializableItemStack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Tag
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
            if (ingredient.tag?.isNotEmpty() == true) {
                val namespacedKey = NamespacedKey.fromString(ingredient.tag) ?: NamespacedKey.minecraft(ingredient.tag)
                recipe.addIngredient(
                    RecipeChoice.MaterialChoice(
                        Bukkit.getTag(
                            Tag.REGISTRY_BLOCKS,
                            namespacedKey,
                            Material::class.java
                        ) ?: Bukkit.getTag(
                            Tag.REGISTRY_ITEMS,
                            namespacedKey,
                            Material::class.java
                        ) ?: Tag.DIRT
                    )
                )
            } else recipe.addIngredient(RecipeChoice.ExactChoice(ingredient.toItemStack()))
        }

        return recipe
    }
}

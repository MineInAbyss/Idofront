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
import org.bukkit.inventory.ShapedRecipe

@Serializable
@SerialName("shaped")
class ShapedRecipeIngredients(
    val items: Map<String, SerializableItemStack>,
    val configuration: String = "",
) : SerializableRecipeIngredients() {
    override fun toRecipe(key: NamespacedKey, result: ItemStack, group: String): Recipe {
        val recipe = ShapedRecipe(key, result)

        recipe.shape(*configuration.replace("|", "").split("\n").toTypedArray())

        recipe.group = group

        items.forEach { (key, ingredient) ->
            if (ingredient.tag !== "") {
                recipe.setIngredient(
                    key[0],
                    RecipeChoice.MaterialChoice(
                        Bukkit.getTag(
                            Tag.REGISTRY_BLOCKS,
                            NamespacedKey.minecraft(ingredient.tag),
                            Material::class.java
                        )
                    )
                )
            } else {
                recipe.setIngredient(key[0], RecipeChoice.ExactChoice(ingredient.toItemStack()))
            }
        }

        return recipe
    }
}

package com.mineinabyss.idofront.serialization.recipes

import com.mineinabyss.idofront.serialization.SerializableItemStack
import com.mineinabyss.idofront.serialization.recipes.options.IngredientOptions
import com.mineinabyss.idofront.serialization.recipes.options.RecipeWithOptions
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.recipe.CraftingBookCategory

@Serializable
@SerialName("shaped")
class ShapedRecipeIngredients(
    val items: Map<String, SerializableItemStack>,
    val configuration: String = "",
) : SerializableRecipeIngredients() {
    override fun toRecipe(key: NamespacedKey, result: ItemStack, group: String, category: String): Recipe? {
        return toRecipeWithOptions(key, result, group, category)?.recipe
    }

    override fun toRecipeWithOptions(
        key: NamespacedKey,
        result: ItemStack,
        group: String,
        category: String,
    ): RecipeWithOptions? {
        val recipe = ShapedRecipe(key, result)

        recipe.shape(*configuration.replace("|", "").split("\n").toTypedArray())

        recipe.group = group
        recipe.category = CraftingBookCategory.entries.find { it.name == category } ?: CraftingBookCategory.MISC

        val options = items.mapNotNull { (key, ingredient) ->
            val choice = if (ingredient.tag?.isNotEmpty() == true) {
                val namespacedKey = NamespacedKey.fromString(ingredient.tag)!!
                RecipeUtils.getMaterialChoiceForTag(namespacedKey)
            } else ingredient.toRecipeChoice()
            recipe.setIngredient(key[0], choice)
            choice to (ingredient.recipeOptions.takeIf { it.isNotEmpty() } ?: return@mapNotNull null)
        }.toMap().takeIf { it.keys.any { it != RecipeChoice.empty() } } ?: return null
        return RecipeWithOptions(recipe, IngredientOptions(options))
    }
}

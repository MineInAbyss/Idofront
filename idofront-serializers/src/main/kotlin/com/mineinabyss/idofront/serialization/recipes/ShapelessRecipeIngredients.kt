package com.mineinabyss.idofront.serialization.recipes

import com.mineinabyss.idofront.serialization.SerializableItemStack
import com.mineinabyss.idofront.serialization.recipes.options.IngredientOptions
import com.mineinabyss.idofront.serialization.recipes.options.RecipeWithOptions
import com.mineinabyss.idofront.serialization.recipes.options.ingredientOptionsListener
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.inventory.recipe.CraftingBookCategory

@Serializable
@SerialName("shapeless")
class ShapelessRecipeIngredients(
    val items: List<SerializableItemStack>,
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
        val recipe = ShapelessRecipe(key, result)

        recipe.group = group
        recipe.category = CraftingBookCategory.entries.find { it.name == category } ?: CraftingBookCategory.MISC

        val options = IngredientOptions(items.associate { ingredient ->
            val choice = when {
                ingredient.tag != null -> RecipeUtils.itemTypeChoiceForTag(ingredient.tag)
                else -> ingredient.toRecipeChoice()
            }
            recipe.addIngredient(choice)

            choice to ingredient.recipeOptions
        }.takeUnless { it.keys.all(RecipeChoice.empty()::equals) } ?: return null)

        ingredientOptionsListener.keyToOptions[key.asString()] = options
        return RecipeWithOptions(recipe, options)
    }
}

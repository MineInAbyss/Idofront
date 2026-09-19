package com.mineinabyss.idofront.serialization.recipes

import com.mineinabyss.idofront.serialization.SerializableDataTypes
import com.mineinabyss.idofront.serialization.SerializableItemStack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.BrewingRecipe
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import org.bukkit.potion.PotionType

@Serializable
@SerialName("brewing")
class BrewingRecipeIngredients(
    private val input: SerializableItemStack = SerializableItemStack(type = Material.POTION, potionContents = SerializableDataTypes.PotionContents(PotionType.WATER)),
    private val ingredient: SerializableItemStack,
) {
    fun toBrewingRecipe(key: NamespacedKey, result: ItemStack): BrewingRecipe {
        return BrewingRecipe(
            key,
            result,
            RecipeChoice.predicateChoice(input::matches, input.toItemStack()),
            RecipeChoice.predicateChoice(ingredient::matches, ingredient.toItemStack())
        )
    }
}

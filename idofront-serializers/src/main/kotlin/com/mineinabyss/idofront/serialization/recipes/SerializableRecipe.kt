package com.mineinabyss.idofront.serialization.recipes

import com.mineinabyss.idofront.serialization.SerializableItemStack
import com.mineinabyss.idofront.util.toMCKey
import kotlinx.serialization.Serializable

@Serializable
class SerializableRecipe(
    val key: String,
    val ingredients: SerializableRecipeIngredients,
    val result: SerializableItemStack,
    val group: String = "",
    val category: String = "MISC"
) {
    fun toCraftingRecipe() {
        ingredients.toRecipe(key.toMCKey(), result.toItemStack(), group, category)
    }
}
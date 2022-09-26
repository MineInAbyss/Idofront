package com.mineinabyss.idofront.recipes

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.inventory.*
import org.bukkit.plugin.java.JavaPlugin

fun Recipe.register() {
    Bukkit.getServer().addRecipe(this)
}

/**
 * Adds a furnace recipe for which takes in [input] and puts out [result]
 *
 * **Warning:** This function uses a deprecated method from the Bukkit API. If things break in the future, only the
 * internal code should change, but this method itself may change in the future!
 *
 * @see addCookingRecipes
 */
fun addFurnaceRecipe(
    name: String,
    input: ItemStack,
    result: ItemStack,
    experience: Float,
    cookingTime: Int,
    plugin: JavaPlugin
) {
    FurnaceRecipe(
        NamespacedKey(plugin, name),
        result,
        RecipeChoice.ExactChoice(input),
        experience,
        cookingTime
    ).register()
}

/**
 * Adds a smoker recipe for which takes in [input] and puts out [result]
 *
 * **Warning:** This function uses a deprecated method from the Bukkit API. If things break in the future, only the
 * internal code should change, but this method itself may change in the future!
 *
 * @see addCookingRecipes
 */
fun addSmokerRecipe(
    name: String,
    input: ItemStack,
    result: ItemStack,
    experience: Float,
    cookingTime: Int,
    plugin: JavaPlugin
) {
    SmokingRecipe(
        NamespacedKey(plugin, name),
        result,
        RecipeChoice.ExactChoice(input),
        experience,
        cookingTime
    ).register()
}

/**
 * Adds a campfire recipe for which takes in [input] and puts out [result]
 *
 * **Warning:** This function uses a deprecated method from the Bukkit API. If things break in the future, only the
 * internal code should change, but this method itself may change in the future!
 *
 * @see addCookingRecipes
 */
fun addCampfireRecipe(
    name: String,
    input: ItemStack,
    result: ItemStack,
    experience: Float,
    cookingTime: Int,
    plugin: JavaPlugin
) {
    CampfireRecipe(
        NamespacedKey(plugin, name),
        result,
        RecipeChoice.ExactChoice(input),
        experience,
        cookingTime
    ).register()
}


/**
 * Adds a furnace, smoker, and campfire recipe for which takes in [input] and puts out [result]
 * The smoker recipe will be twice as fast, while campfire will be three times as slow (like vanilla food items)
 *
 * **Warning:** This function uses a deprecated method from the Bukkit API. If things break in the future, only the
 * internal code should change, but this method itself may change in the future!
 */
fun addCookingRecipes(
    name: String,
    input: ItemStack,
    result: ItemStack,
    experience: Float,
    cookingTime: Int,
    plugin: JavaPlugin
) {
    addFurnaceRecipe(name, input, result, experience, cookingTime, plugin)
    addSmokerRecipe(name, input, result, experience, cookingTime / 2, plugin)
    addCampfireRecipe(name, input, result, experience, cookingTime * 3, plugin)
}

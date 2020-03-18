package com.mineinabyss.idofront.recpies

import org.bukkit.NamespacedKey
import org.bukkit.inventory.FurnaceRecipe
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.RecipeChoice
import org.bukkit.plugin.java.JavaPlugin

fun Recipe.register(plugin: JavaPlugin) {
    plugin.server.addRecipe(this)
}

/**
 * This class uses a deprecated method from the Bukkit API. If things break in the future, only the internal code should
 * change, but this method itself may change in the future!
 */
fun addFurnaceRecipe(name: String, input: ItemStack, output: ItemStack, experience: Float, cookingTime: Int, plugin: JavaPlugin) {
    FurnaceRecipe(NamespacedKey(plugin, name), input, RecipeChoice.ExactChoice(output), experience, cookingTime).register(plugin)
}
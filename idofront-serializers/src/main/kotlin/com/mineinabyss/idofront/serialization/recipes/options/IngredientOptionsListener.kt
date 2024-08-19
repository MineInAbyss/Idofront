package com.mineinabyss.idofront.serialization.recipes.options

import com.mineinabyss.idofront.di.DI
import org.bukkit.Bukkit
import org.bukkit.Keyed
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.plugin.Plugin

val ingredientOptionsListener by DI.observe<IngredientOptionsListener>()

class IngredientOptionsListener(
    val plugin: Plugin,
) : Listener {
    val keyToOptions = mutableMapOf<String, IngredientOptions>()

    @EventHandler
    fun CraftItemEvent.onCraft() {
        val recipe = recipe as? Keyed ?: return
        val options = keyToOptions[recipe.key.asString()] ?: return
        val onCrafted = inventory.matrix.mapIndexedNotNull { index, itemStack ->
            if (itemStack == null) return@mapIndexedNotNull null
            val itemOptions = options.getOptionsFor(itemStack)
            IngredientInfo(itemStack.clone(), index, itemOptions)
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin) {
            val matrix = inventory.matrix
            onCrafted.forEach { (item, slot, options) ->
                options.forEach { it.onCrafted(item) { matrix[slot] = it } }
            }
            inventory.matrix = matrix
        }
    }
}

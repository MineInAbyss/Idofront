package com.mineinabyss.idofront

import com.mineinabyss.idofront.di.DI
import com.mineinabyss.idofront.plugin.listeners
import com.mineinabyss.idofront.serialization.recipes.options.IngredientOptionsListener
import org.bukkit.plugin.java.JavaPlugin

class IdofrontPlugin : JavaPlugin() {
    override fun onEnable() {
        val recipeOptionsListener = IngredientOptionsListener(this)
        DI.add(recipeOptionsListener)
        listeners(recipeOptionsListener)
    }
}

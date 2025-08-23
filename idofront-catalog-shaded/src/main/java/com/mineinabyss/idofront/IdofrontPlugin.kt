package com.mineinabyss.idofront

import com.mineinabyss.idofront.di.DI
import com.mineinabyss.idofront.plugin.Services
import com.mineinabyss.idofront.plugin.listeners
import com.mineinabyss.idofront.serialization.recipes.options.IngredientOptionsListener
import com.mineinabyss.idofront.services.SerializableItemStackService
import com.mineinabyss.idofront.services.impl.SerializableItemStackServiceImpl
import org.bukkit.plugin.java.JavaPlugin

class IdofrontPlugin : JavaPlugin() {
    override fun onLoad() {
        Services.register<SerializableItemStackService>(this, SerializableItemStackServiceImpl())
    }

    override fun onEnable() {
        val recipeOptionsListener = IngredientOptionsListener(this)
        DI.add(recipeOptionsListener)
        listeners(recipeOptionsListener)
    }
}

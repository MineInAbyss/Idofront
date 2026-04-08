package com.mineinabyss.idofront

import com.mineinabyss.dependencies.DI
import com.mineinabyss.dependencies.get
import com.mineinabyss.dependencies.single
import com.mineinabyss.idofront.features.singlePluginLogger
import com.mineinabyss.idofront.plugin.Services
import com.mineinabyss.idofront.plugin.listeners
import com.mineinabyss.idofront.serialization.recipes.options.IngredientOptionsListener
import com.mineinabyss.idofront.services.SerializableItemStackService
import com.mineinabyss.idofront.services.impl.SerializableItemStackServiceImpl
import org.bukkit.plugin.java.JavaPlugin

class IdofrontPlugin : JavaPlugin(), DI {
    override val di = DI {
        singlePluginLogger(this@IdofrontPlugin)
        single { IngredientOptionsListener(this@IdofrontPlugin) }
        single<SerializableItemStackService> { SerializableItemStackServiceImpl() }
    }

    override fun onLoad() {
        Services.register<SerializableItemStackService>(this, get())
    }

    override fun onEnable() {
        listeners(get<IngredientOptionsListener>())
    }
}

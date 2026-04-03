package com.mineinabyss.idofront

import com.mineinabyss.idofront.messaging.ComponentLogger
import com.mineinabyss.idofront.plugin.Services
import com.mineinabyss.idofront.plugin.listeners
import com.mineinabyss.idofront.serialization.recipes.options.IngredientOptionsListener
import com.mineinabyss.idofront.services.SerializableItemStackService
import com.mineinabyss.idofront.services.impl.SerializableItemStackServiceImpl
import org.bukkit.plugin.java.JavaPlugin
import org.kodein.di.*

class IdofrontPlugin : JavaPlugin(), DIAware {
    override val di = DI {
        bindSingleton { ComponentLogger.forPlugin(this@IdofrontPlugin) }
        bindSingleton { IngredientOptionsListener(this@IdofrontPlugin) }
        bindSingleton { SerializableItemStackServiceImpl() }
    }

    val direct = di.direct

    override fun onLoad() {
        Services.register<SerializableItemStackService>(this, direct.instance())
    }

    override fun onEnable() {
        listeners(direct.instance<IngredientOptionsListener>())
    }
}

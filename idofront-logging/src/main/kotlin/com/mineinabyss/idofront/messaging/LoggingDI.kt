package com.mineinabyss.idofront.messaging

import com.mineinabyss.idofront.di.*
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin

fun DIContext.observeLogger() = observe<ComponentLogger>()
    .default {
        if (this is ScopedDIContext) {
            if (byClass != null && IdoLogging.BUKKIT_LOADED) {
                val matchedPlugin = Bukkit.getPluginManager().plugins.find { it::class == byClass }
                if (matchedPlugin != null) return@default ComponentLogger.forPlugin(matchedPlugin)
            }
            return@default ComponentLogger.fallback(tag = simpleName)
        }
        ComponentLogger.fallback()
    }

fun Plugin.injectedLogger(): ComponentLogger = observeLogger().get()
fun Plugin.observeLogger(): DefaultingModuleObserver<ComponentLogger> = DI.scoped(this::class).observeLogger()
fun Plugin.injectLogger(logger: ComponentLogger): Unit = DI.scoped(this::class)
    .add<ComponentLogger>(logger)

package com.mineinabyss.idofront.features

import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import com.mineinabyss.idofront.commands.brigadier.IdoRootCommand
import com.mineinabyss.idofront.plugin.listeners
import com.mineinabyss.idofront.plugin.unregisterListeners
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.koin.core.Koin
import org.koin.core.scope.Scope

@DslMarker
annotation class FeatureDSLMarker

@FeatureDSLMarker
interface FeatureDSL

fun feature(name: String, block: FeatureBuilder.() -> Unit): Feature {
    return FeatureBuilder(name).apply(block).build()
}

data class MainCommand(
    val names: List<String>,
    val description: String?,
    val permission: String?,
) {
    internal val subcommands = mutableListOf<context(Koin) IdoRootCommand.() -> Unit>()
    fun subcommand(block: context(Koin) IdoRootCommand.() -> Unit) {
        subcommands += block
    }
}

class FeatureCreate(val scope: Scope) : FeatureDSL {
    private val plugin = scope.get<Plugin>()

    private val listeners = mutableListOf<Listener>()

    fun listeners(vararg listeners: Listener) {
        this.listeners += listeners
        for (listener in listeners) {
            plugin.server.pluginManager.registerSuspendingEvents(listener, plugin)
        }
    }

    inline fun <reified T : Any> get(): T {
        return scope.get<T>()
    }

    fun close() {
        plugin.unregisterListeners(*listeners.toTypedArray())
        scope.close()
    }
}

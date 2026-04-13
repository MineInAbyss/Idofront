package com.mineinabyss.idofront.features

import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import com.mineinabyss.dependencies.*
import com.mineinabyss.idofront.plugin.Plugins
import com.mineinabyss.idofront.plugin.unregisterListeners
import kotlinx.coroutines.Job
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin

/**
 * Gets the injected [Plugin] instance in this context.
 */
context(di: MutableDI)
inline val plugin get() = di.get<Plugin>()

/**
 * Registers a list of [Listener]s under the [plugin] in this context.
 * Automatically unregisters the listeners on [close].
 * Supports listeners with `suspend` functions via MCCoroutine.
 *
 * A common pattern is to use [new] to create instances of listeners that need constructor arguments:
 *
 * ```kotlin
 * listeners(new(::MyListener))
 * ```
 */
fun MutableDI.listeners(vararg listeners: Listener) {
    val plugin = get<Plugin>()
    val manager = plugin.server.pluginManager

    addCloseable { plugin.unregisterListeners(*listeners) }
    for (listener in listeners) {
        try {
            manager.registerSuspendingEvents(listener, plugin)
        } catch (_: IllegalArgumentException) {
            // Fallback in mocked tests where MCCoroutine can't correctly inject
            manager.registerEvents(listener, plugin)
        }
    }
}

/**
 * Checks if all the passed plugin [names] are enabled, throws an error if not.
 */
fun requirePlugins(vararg names: String) {
    val notEnabled = names.filterNot { Plugins.isEnabled(it) }
    require(notEnabled.isEmpty()) { "Plugin dependencies not found $notEnabled" }
}

/**
 * Ensures [job] is cancelled on [close].
 */
fun MutableDI.task(job: Job) {
    addCloseable { job.cancel() }
}

/**
 * Only runs the block on the server startup (before the first server tick).
 *
 * Useful for registering things that usually go in a Plugin's onLoad like commands.
 */
inline fun MutableDI.onServerStartup(block: () -> Unit) {
    if (Bukkit.getCurrentTick() == 0) {
        block()
    }
}


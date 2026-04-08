package com.mineinabyss.idofront.features

import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import com.mineinabyss.dependencies.*
import com.mineinabyss.idofront.plugin.Plugins
import com.mineinabyss.idofront.plugin.unregisterListeners
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin

context(di: MutableDI)
inline val plugin get() = di.get<Plugin>()

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

fun requirePlugins(vararg names: String) {
    val notEnabled = names.filterNot { Plugins.isEnabled(it) }
    require(notEnabled.isEmpty()) { "Plugin dependencies not found $notEnabled" }
}

fun MutableDI.task(job: Job) {
    val scope = get<CoroutineScope>()
    scope.launch { job.join() }
}

//fun MutableDI.commands(block: context(DICommandContext) RootIdoCommands.() -> Unit) = once {
//    val context = DICommandContext(get(), get())
//    get<Plugin>().commands {
//        block(context, this)
//    }
//}

inline fun MutableDI.onServerStartup(block: () -> Unit) {
    if (Bukkit.getCurrentTick() == 0) {
        block()
    }
}


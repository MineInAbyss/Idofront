package com.mineinabyss.idofront.features

import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import com.mineinabyss.features.*
import com.mineinabyss.idofront.commands.brigadier.IdoRootCommand
import com.mineinabyss.idofront.commands.brigadier.RootIdoCommands
import com.mineinabyss.idofront.commands.brigadier.commands
import com.mineinabyss.idofront.commands.brigadier.context.IdoCommandContext
import com.mineinabyss.idofront.plugin.unregisterListeners
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.kodein.di.DirectDI
import org.kodein.di.direct
import org.kodein.di.instance

context(di: DirectDI)
inline val plugin get() = di.instance<Plugin>()

fun FeatureDI.listeners(vararg listeners: Listener) {
    val plugin = instance<Plugin>()
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

fun FeatureDI.task(job: Job) {
    val scope = instance<CoroutineScope>()
    scope.launch { job.join() }
}

data class DICommandContext(val manager: FeatureManager, val feature: Feature<*>)

context(di: DICommandContext)
inline fun <reified T : Any> IdoCommandContext.get(): T = di.manager.getInstance(di.feature)?.di?.direct?.instance<T>() ?: error("Command tried to get feature config of an unloaded feature: ${di.feature.name}.")

fun FeatureBuilder.commands(block: context(DICommandContext) RootIdoCommands.() -> Unit) {
    onLoad {
        val context = DICommandContext(instance(), instance())
        instance<Plugin>().commands {
            block(context, this)
        }
    }
}

fun FeatureBuilder.mainCommand(block: context(DICommandContext) IdoRootCommand.() -> Unit) {
    onLoad {
        instance<MainCommand>().subcommand(block)
    }
}

data class MainCommand(
    val names: List<String>,
    val description: String?,
    val reloadCommandName: String? = null,
    val reloadCommandPermission: String? = null,
    val permission: String? = null,
) {
    internal val subcommands = mutableListOf<context(DICommandContext) IdoRootCommand.() -> Unit>()
    fun subcommand(block: context(DICommandContext) IdoRootCommand.() -> Unit) {
        subcommands += block
    }
}

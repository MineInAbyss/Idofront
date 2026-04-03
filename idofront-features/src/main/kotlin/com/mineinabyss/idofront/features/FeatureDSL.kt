package com.mineinabyss.idofront.features

import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import com.mineinabyss.features.*
import com.mineinabyss.idofront.commands.brigadier.*
import com.mineinabyss.idofront.commands.brigadier.context.IdoCommandContext
import com.mineinabyss.idofront.messaging.error
import com.mineinabyss.idofront.messaging.success
import com.mineinabyss.idofront.plugin.Plugins
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


fun FeatureBuilder.FeatureDependenciesBuilder.plugins(vararg names: String) {
    condition {
        val notEnabled = names.filterNot { Plugins.isEnabled(it) }
        require(notEnabled.isEmpty()) { "Plugin dependencies not found: $notEnabled" }
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

val MainCommandFeature = feature("Main Command") {
    onLoad {
        plugin.commands {
            val main = get<MainCommand>()
            val manager = get<FeatureManager>()
            main.names.invoke {
                description = main.description
                permission = main.permission
                main.subcommands.forEach { subcommand ->
                    val feature = manager.getNamed(subcommand.featureName) ?: error("Feature name not found: ${subcommand.featureName}")
                    val context = DICommandContext(manager, feature)
                    subcommand.create(context, this)
                }

                if (main.reloadCommandName != null) {
                    main.reloadCommandName {
                        permission = main.reloadCommandPermission

                        executes {
                            manager.reloadAll()
                        }

                        executes.args(
                            "feature" to Args.string().oneOf { manager.loaded.map { it.name }.toList() }
                        ) { featureName ->
                            if (manager.reload(manager.getNamed(featureName) ?: fail("Feature $featureName not found"))) {
                                sender.success("Reloaded feature $featureName")
                            } else {
                                sender.error("Failed to reload feature $featureName")
                            }
                        }
                    }
                }
            }
        }
    }
}

fun FeatureBuilder.mainCommand(block: context(DICommandContext) IdoRootCommand.() -> Unit) {
    onLoad {
        instance<MainCommand>().subcommand(name, block)
    }
}

data class MainCommand(
    val names: List<String>,
    val description: String?,
    val reloadCommandName: String? = null,
    val reloadCommandPermission: String? = null,
    val reloadableFeatures: List<Feature<*>>? = null,
    val permission: String? = null,
) {
    internal val subcommands = mutableListOf<Subcommand>()
    fun subcommand(featureName: String, block: context(DICommandContext) IdoRootCommand.() -> Unit) {
        subcommands += Subcommand(featureName, block)
    }
}

data class Subcommand(
    val featureName: String,
    val create: context(DICommandContext) IdoRootCommand.() -> Unit,
)

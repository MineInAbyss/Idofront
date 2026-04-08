package com.mineinabyss.idofront.features

import com.mineinabyss.dependencies.DI
import com.mineinabyss.dependencies.DIScope
import com.mineinabyss.dependencies.get
import com.mineinabyss.dependencies.module
import com.mineinabyss.idofront.commands.brigadier.Args
import com.mineinabyss.idofront.commands.brigadier.IdoRootCommand
import com.mineinabyss.idofront.commands.brigadier.commands
import com.mineinabyss.idofront.commands.brigadier.context.IdoCommandContext
import com.mineinabyss.idofront.commands.brigadier.oneOf
import com.mineinabyss.idofront.messaging.error
import com.mineinabyss.idofront.messaging.success

data class DICommandContext(val scope: DIScope, val module: DI.Module)

data class MainCommand(
    val names: List<String>,
    val description: String?,
    val reloadCommandName: String? = null,
    val reloadCommandPermission: String? = null,
    val reloadableFeatures: List<DI.Module>? = null,
    val permission: String? = null,
) {
    internal val subcommands = mutableListOf<Subcommand>()
    fun subcommand(module: DI.Module, block: context(DICommandContext) IdoRootCommand.() -> Unit) {
        subcommands += Subcommand(module, block)
    }
}

data class Subcommand(
    val module: DI.Module,
    val create: context(DICommandContext) IdoRootCommand.() -> Unit,
)

context(di: DICommandContext)
inline fun <reified T : Any> IdoCommandContext.get(): T = di
    .scope[di.module]
    ?.get<T>() ?: error("Command tried to get feature config of an unloaded feature: ${di.module.name}.")

val MainCommandFeature = module("Main Command") {
    onServerStartup {
        plugin.commands {
            val main = get<MainCommand>()
            val manager = get<DIScope>()
            main.names.invoke {
                description = main.description
                permission = main.permission
                main.subcommands.forEach { subcommand ->
//                val feature = manager.getModule(subcommand.module) ?: error("Feature name not found: ${subcommand.module}")
                    val context = DICommandContext(manager, subcommand.module)
                    subcommand.create(context, this)
                }

                if (main.reloadCommandName != null) {
                    main.reloadCommandName {
                        permission = main.reloadCommandPermission

                        executes {
                            if (main.reloadableFeatures == null)
                                manager.reloadAll()
                            else manager.reload(*main.reloadableFeatures.toTypedArray())
                        }

                        executes.args(
                            "feature" to Args.string().oneOf {
                                if (main.reloadableFeatures == null)
                                    manager.loaded.map { it.name }.toList()
                                else main.reloadableFeatures.map { it.name }
                            }
                        ) { featureName ->
                            val feat = manager.loaded.find { it.name == featureName } ?: fail("Feature $featureName not found")
                            if (runCatching { manager.reload(feat) }.onFailure { it.printStackTrace() }.isSuccess) {
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

/**
 * Registers a subcommand under this plugin's [MainCommand].
 */
fun DI.Module.mainCommand(block: context(DICommandContext) IdoRootCommand.() -> Unit): DI.Module {
    return override {
        onServerStartup {
            get<MainCommand>().subcommand(this@mainCommand, block)
        }
    }
}

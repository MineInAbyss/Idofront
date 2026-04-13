package com.mineinabyss.idofront.features

import com.mineinabyss.dependencies.DI
import com.mineinabyss.dependencies.DIScope
import com.mineinabyss.dependencies.get
import com.mineinabyss.dependencies.module
import com.mineinabyss.idofront.commands.brigadier.*
import com.mineinabyss.idofront.messaging.error
import com.mineinabyss.idofront.messaging.success
import org.bukkit.plugin.Plugin

/**
 * Lets other features register subcommands under this plugin's [MainCommand].
 *
 * @see mainCommand
 */
val MainCommandFeature = module("Main Command") {
    onServerStartup {
        plugin.commands {
            val main = get<MainCommand>()
            val manager = get<DIScope>()
            main.names.invoke {
                description = main.description
                permission = main.permission
                main.subcommands.forEach { subcommand ->
                    // Skip commands that failed to load
                    if (manager.getOrNull(subcommand.module) == null) return@forEach

//                val feature = manager.getModule(subcommand.module) ?: error("Feature name not found: ${subcommand.module}")
                    val context = DICommandContext(manager, subcommand.module)
                    subcommand.create(context, this)
                }

                if (main.reloadCommandName != null) {
                    main.reloadCommandName {
                        permission = main.reloadCommandPermission

                        executes {
                            main.onBeforeReload()
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
                            main.onBeforeReload()
                            if (manager.reload(feat).isSuccess) {
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
 *
 * Plugins using this must first inject a [MainCommand] into their DI container, then load [MainCommandFeature] *after* all other features are loaded:
 *
 * ```kotlin
 * val di = DI {
 *   single { MainCommand(names = listOf("mycommand"), ...) }
 * }
 *
 * val FeatureA = module("Feature A") { ... }.mainCommand {
 *   "subcommand" { ... }
 * }
 *
 * di.scope.loadAllCatching(FeatureA, FeatureB, ...)
 * di.scope.load(MainCommandFeature)
 */
fun DI.Module.mainCommand(block: context(DICommandContext) IdoRootCommand.() -> Unit): DI.Module {
    return override {
        onServerStartup {
            get<MainCommand>().subcommand(this@mainCommand, block)
        }
    }
}

/**
 * Registers new commands under the [plugin] in this context, unlike [mainCommand], these are top-level commands.
 *
 * The entire block is only evaluated once on startup, use [DICommandContext]'s `get` to get dependencies in commands.
 */
fun DI.Module.commands(block: context(DICommandContext) RootIdoCommands.() -> Unit) = override {
    onServerStartup {
        val context = DICommandContext(get(), get())
        get<Plugin>().commands {
            block(context, this)
        }
    }
}

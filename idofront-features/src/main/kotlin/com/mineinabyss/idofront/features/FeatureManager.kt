package com.mineinabyss.idofront.features

import com.mineinabyss.idofront.commands.arguments.optionArg
import com.mineinabyss.idofront.commands.execution.IdofrontCommandExecutor
import com.mineinabyss.idofront.messaging.error
import com.mineinabyss.idofront.messaging.logError
import com.mineinabyss.idofront.messaging.logSuccess
import com.mineinabyss.idofront.messaging.success
import com.mineinabyss.idofront.plugin.Plugins
import com.mineinabyss.idofront.plugin.actions
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.plugin.java.JavaPlugin
import kotlin.reflect.KClass

abstract class FeatureManager<T : FeatureDSL>(
    plugin: JavaPlugin,
    contextClass: KClass<T>
) : FeatureWithContext<T>(contextClass) {
    val commandExecutor: IdofrontCommandExecutor = object : IdofrontCommandExecutor(), TabCompleter {
        override val commands = commands(plugin) {
            context.mainCommandProvider(this).apply {
                if (this == null) return@apply
                // subcommands
                context.mainCommandExtras.forEach { subcommand -> subcommand() }
            }
            // extra root commands
            context.rootCommandExtras.forEach { it() }
        }

        override fun onTabComplete(
            sender: CommandSender,
            command: org.bukkit.command.Command,
            alias: String,
            args: Array<String>
        ): List<String> {
            val tab = TabCompletion(sender, command, alias, args)
            return context.tabCompletions.mapNotNull { it(tab) }.flatten()
        }
    }

    fun enable() = actions {
        "Creating feature manager context" {
            createAndInjectContext()
        }
        "Registering feature contexts" {
            context.features
                .filterIsInstance<FeatureWithContext<*>>()
                .forEach {
                    runCatching { it.createAndInjectContext() }
                        .onFailure { e -> logError("Failed to create context for ${it::class.simpleName}: $e") }
                }
        }
        context.features.forEach { feature ->
            val featureName = feature::class.simpleName
            when (dependsOn.all { Plugins.isEnabled(it) }) {
                true -> "Enabled $featureName" {
                    feature.enable(context)
                }.onFailure(Throwable::printStackTrace)

                false -> logError(
                    "Could not enable $featureName, missing dependencies: ${dependsOn.filterNot(Plugins::isEnabled)}"
                )
            }
        }

        with(context) {
            mainCommand {
                "reloadFeature" {
                    val featureName by optionArg(features.map { it::class.simpleName!! })
                    action {
                        reloadFeature(featureName, sender)
                    }
                }
                "reload" {
                    context.disable()
                    context.enable()
                }
            }
            tabCompletion {
                when (args.size) {
                    1 -> listOf("reloadFeature").filter { it.startsWith(args[0], ignoreCase = true) }

                    2 -> when (args[0]) {
                        "reloadFeature" -> features.map { it::class.simpleName!! }
                            .filter { it.startsWith(args[1], ignoreCase = true) }

                        else -> null
                    }

                    else -> null
                }
            }
        }
        enable(context)
    }

    fun disable() = actions {
        disable(context)
        "Disabling features" {
            context.features.forEach { feature ->
                runCatching { feature.disable(context) }
                    .onSuccess { logSuccess("Disabled ${feature::class.simpleName}") }
                    .onFailure { e -> logError("Failed to disable ${feature::class.simpleName}: $e") }
            }
        }
        removeContext()
    }

    fun reloadFeature(simpleClassName: String, sender: CommandSender) {
        val feature = context.features
            .find { it::class.simpleName == simpleClassName }
            ?: error("Feature not found $simpleClassName")

        with(feature) {
            runCatching { context.disable() }
                .onSuccess { sender.success("$simpleClassName: Disabled") }
                .onFailure { sender.error("$simpleClassName: Failed to disable, $it") }
            if (feature is FeatureWithContext<*>)
                runCatching { feature.createAndInjectContext() }
                    .onSuccess { sender.success("$simpleClassName: Recreated context") }
                    .onFailure { sender.error("$simpleClassName: Failed to recreate context, $it") }
            runCatching { context.enable() }
                .onSuccess { sender.success("$simpleClassName: Enabled") }
                .onFailure { sender.error("$simpleClassName: Failed to enable, $it") }
        }
    }

    fun reload() {
        disable()
        enable()
    }
}

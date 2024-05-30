package com.mineinabyss.idofront.features

import com.mineinabyss.idofront.commands.arguments.optionArg
import com.mineinabyss.idofront.commands.execution.IdofrontCommandExecutor
import com.mineinabyss.idofront.messaging.ComponentLogger
import com.mineinabyss.idofront.messaging.error
import com.mineinabyss.idofront.messaging.observeLogger
import com.mineinabyss.idofront.messaging.success
import com.mineinabyss.idofront.plugin.Plugins
import com.mineinabyss.idofront.plugin.actions
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.plugin.java.JavaPlugin

abstract class FeatureManager<T : FeatureDSL>(
    val plugin: JavaPlugin,
    createContext: () -> T,
) : FeatureWithContext<T>(createContext) {
    val logger: ComponentLogger by plugin.observeLogger()

    val commandExecutor: IdofrontCommandExecutor by lazy {
        object : IdofrontCommandExecutor(), TabCompleter {
            override val commands = commands(plugin) {
                context.mainCommandProvider(this) {
                    mainCommandExtras.forEach { it() }
                    context.features.forEach { feature -> feature.mainCommandExtras.forEach { it() } }
                }
            }

            override fun onTabComplete(
                sender: CommandSender,
                command: org.bukkit.command.Command,
                alias: String,
                args: Array<String>
            ): List<String> {
                val tab = TabCompletion(sender, command, alias, args)
                return (context.features + this@FeatureManager).flatMap { it.tabCompletions }.mapNotNull { it(tab) }
                    .flatten()
            }
        }
    }

    fun load() = actions(logger) {
        "Loading features" {
            context.features.forEach { feature ->
                "${feature::class.simpleName}" {
                    feature.load(context)
                }
            }
        }
    }

    fun enable() = actions(logger) {
        "Creating feature manager context" {
            createAndInjectContext()
        }

        val featuresWithMetDeps = context.features.filter { feature -> feature.dependsOn.all { Plugins.isEnabled(it) } }
        (context.features - featuresWithMetDeps.toSet()).forEach { feature ->
            val featureName = feature::class.simpleName
            logger.f("Could not enable $featureName, missing dependencies: ${feature.dependsOn.filterNot(Plugins::isEnabled)}")
        }
        "Registering feature contexts" {
            featuresWithMetDeps
                .filterIsInstance<FeatureWithContext<*>>()
                .forEach {
                    runCatching {
                        it.createAndInjectContext()
                    }.onFailure { error -> logger.f("Failed to create context for ${it::class.simpleName}: $error") }
                }
        }

        featuresWithMetDeps.forEach { feature ->
            val featureName = feature::class.simpleName
            "Enabled $featureName" {
                feature.enable(context)
            }.onFailure(Throwable::printStackTrace)
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
        commandExecutor
    }

    fun disable() = actions(logger) {
        disable(context)
        "Disabling features" {
            context.features.forEach { feature ->
                runCatching { feature.disable(context) }
                    .onSuccess { logger.s("Disabled ${feature::class.simpleName}") }
                    .onFailure { e -> logger.f("Failed to disable ${feature::class.simpleName}: $e") }
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

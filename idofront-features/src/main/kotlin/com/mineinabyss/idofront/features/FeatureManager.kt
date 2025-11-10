package com.mineinabyss.idofront.features

import co.touchlab.kermit.Logger
import com.mineinabyss.idofront.commands.brigadier.Args
import com.mineinabyss.idofront.commands.brigadier.commands
import com.mineinabyss.idofront.commands.brigadier.oneOf
import com.mineinabyss.idofront.messaging.ComponentLogger
import com.mineinabyss.idofront.plugin.Plugins
import com.mineinabyss.idofront.plugin.actions
import org.bukkit.plugin.Plugin
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.binds
import org.koin.dsl.koinApplication
import org.koin.dsl.module

fun Plugin.featureManager(setup: FeatureManagerBuilder.() -> Unit): FeatureManager {
    return FeatureManagerBuilder(this).apply(setup).build()
}

class FeatureManager(
    val plugin: Plugin,
    val logger: ComponentLogger,
    globalModule: Module.() -> Unit,
    installed: List<Feature>,
    mainCommand: MainCommand?,
) {
    private val mainFeature: Feature = feature("main") {
        mainCommand {
            "reload" {
                executes {
                    reload()
                }

                executes.args("feature" to Args.string().oneOf { installed.map { it.name }.toList() }) { featureName ->
                    reload(getFeature(featureName) ?: return@args)
                    // TODO Send reload success/fail
                }
            }
        }

        onLoad {
            plugin.commands {
                val main = get<MainCommand>()
                main.names.invoke {
                    description = main.description
                    permission = main.permission
                    main.subcommands.forEach { subcommand -> subcommand(application.koin, this) }
                }
            }
        }
    }

    private val application = koinApplication {
        // Set up global context
        modules(module {
            if (mainCommand != null) single<MainCommand> { mainCommand }
            single<FeatureManager> { this@FeatureManager }
            single<Plugin> { plugin }
            single { logger } binds arrayOf(ComponentLogger::class, Logger::class)
            globalModule()
        })
    }

    private val features = (installed + mainFeature)
        .filter { feature ->
            val unmetPluginDeps = feature.dependencies.plugins.filterNot(Plugins::isEnabled)
            if (unmetPluginDeps.isNotEmpty()) {
                logger.f("Failed to load ${feature.name}, missing dependencies: [${unmetPluginDeps.joinToString()}]")
                return@filter false
            }
            if (!feature.dependencies.conditions(application.koin)) {
                return@filter false
            }

            true
        }

    init {
        // Set up scoped context per feature
        features.forEach { feature ->
            application.modules(module {
                runCatching {
                    feature.globalModule(this)
                }.onFailure {
                    logger.e { "Error while creating global module for feature ${feature.name}" }
                    it.printStackTrace()
                }

                scope(named(feature.name)) {
                    runCatching {
                        feature.scopedModule(this)
                    }.onFailure {
                        logger.e { "Error while creating scoped module for feature ${feature.name}" }
                        it.printStackTrace()
                    }
                }
            })
        }
    }
    private val scopes = mutableMapOf<Feature, Scope>() //TODO just use koin's scopes directly?

    private val enabledFeatures = mutableMapOf<Feature, FeatureCreate>()

    fun load() = actions(logger) {
        logger.i { "Creating all feature scopes..." }
        val scopes = features.associateWith { feature ->
            application.koin.createScope(feature.name, named(feature.name))
        }

        logger.i { "Linking feature scopes..." }
        scopes.forEach { (feature, scope) ->
            scope.linkTo(*feature.dependencies.features.map { scopes.getValue(it) }.toTypedArray())
        }

        logger.i { "Loading all features..." }
        scopes.keys.forEach { feature ->
            load(feature)
        }
        this@FeatureManager.scopes.putAll(scopes)
    }

    fun getFeature(name: String): Feature? = features.find { it.name == name }

    fun getScope(feature: Feature) = scopes[feature] ?: error("Cannot get scope of '${feature.name}', it is not enabled")

    inline fun <reified T : Any> get(feature: Feature): T? {
        return getScope(feature).getOrNull<T>()
    }

    fun enable() = actions(logger) {
        features.forEach { feature ->
            enable(feature)
        }
    }

    fun disable() = actions(logger) {
        features.forEach { feature ->
            disable(feature)
        }
    }

    fun load(feature: Feature) = actions(logger) {
        if (feature !in features) {
            logger.f("Feature ${feature.name} is not installed")
            return@actions
        }
        runCatching {
            feature.onLoad(application.koin)
        }.onFailure {
            logger.f("Failed to load ${feature.name}")
            it.printStackTrace()
        }
    }

    fun enable(feature: Feature) = actions(logger) {
        if (feature !in features) {
            logger.f("Feature ${feature.name} is not installed")
            return@actions
        }
        "Enabled ${feature.name}" {
            val scope = scopes.getOrPut(feature) {
                application.koin.createScope(feature.name, named(feature.name))
                    .also { it.linkTo(*feature.dependencies.features.map { scopes.getValue(it) }.toTypedArray()) }
            }
            val featureScope = FeatureCreate(scope)
            feature.onEnable(featureScope)
            enabledFeatures[feature] = featureScope
        }.onFailure(Throwable::printStackTrace)
    }

    fun disable(feature: Feature) = actions(logger) {
        if (feature !in enabledFeatures.keys) {
            logger.f("Feature ${feature.name} is already disabled")
            return@actions
        }
        "Disabled ${feature.name}" {
            val featureScope = enabledFeatures.getValue(feature)
            feature.onDisable(featureScope)
            enabledFeatures.remove(feature)
            scopes.remove(feature)
            featureScope.close()
        }.onFailure(Throwable::printStackTrace)
    }

    fun reload(feature: Feature) = actions(logger) {
        disable(feature)
        enable(feature)
    }

    fun reload() {
        disable()
        enable()
    }
}

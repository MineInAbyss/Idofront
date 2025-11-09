package com.mineinabyss.idofront.features

import com.mineinabyss.idofront.commands.brigadier.Args
import com.mineinabyss.idofront.commands.brigadier.executes
import com.mineinabyss.idofront.commands.brigadier.suggests
import com.mineinabyss.idofront.messaging.ComponentLogger
import com.mineinabyss.idofront.plugin.Plugins
import com.mineinabyss.idofront.plugin.actions
import org.bukkit.plugin.Plugin
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.koinApplication
import org.koin.dsl.module

fun featureManager(setup: FeatureManagerBuilder.() -> Unit): FeatureManager {
    TODO()
}

class FeatureManagerBuilder() {
    fun globalModule(block: Module.() -> Unit) {
        TODO()
    }

    fun withMainCommand(vararg names: String, description: String? = null) {

    }

    fun install(vararg features: Feature) {

    }

    fun build(): FeatureManager {
        TODO()
    }
}

class FeatureManager(
    val plugin: Plugin,
    val logger: ComponentLogger,
    installed: List<Feature>,
) {
//    private val mainCommand = plugin.commands {
//        "test" {
//
//        }
//    }

    private val mainFeature: Feature = feature("main") {
        mainCommand {
            "feature" {
                val featureArg = Args.string().suggests { suggestFiltering(installed.map { it.name }.toList()) }
                "reload" {
                    executes {
                        reload()
                    }

                    executes(featureArg) { featureName ->
                        reload(getFeature(featureName) ?: return@executes)
                        // TODO Send reload success/fail
                    }
                }
            }
        }
    }
    private val features = (installed + mainFeature).associateBy { it.name }

    private val application = koinApplication {
        features.values.forEach { feature ->
            modules(module(createdAtStart = true) {
                feature.globalModule(this)

                scope(named(feature.name)) {
                    feature.scopedModule(this)
                }
            })
        }
    }
    private val scopes = mutableMapOf<Feature, Scope>()
    private val enabledFeatures = mutableMapOf<Feature, FeatureCreate>()

    fun getFeature(name: String): Feature? = features[name]

    fun load() = actions(logger) {
        logger.i { "Creating all feature scopes..." }
        val scopes = features.values.associateWith { feature ->
            application.koin.createScope(feature.name, named(feature.name))
        }

        logger.i { "Linking feature scopes..." }
        scopes.forEach { (feature, scope) ->
            scope.linkTo(*feature.dependencies.features.map { scopes.getValue(it) }.toTypedArray())
        }

        this@FeatureManager.scopes.putAll(scopes)
    }

    fun getScope(feature: Feature) = scopes.getValue(feature)

    fun enable() = actions(logger) {
        features.values.forEach { feature ->
            enable(feature)
        }
    }

    fun disable() = actions(logger) {
        features.values.forEach { feature ->
            disable(feature)
        }
    }

    fun enable(feature: Feature) = actions(logger) {
        if (feature !in features.values) {
            logger.f("Feature ${feature.name} is not installed")
            return@actions
        }
        val unmetPluginDeps = feature.dependencies.plugins.filterNot(Plugins::isEnabled)
        if (unmetPluginDeps.isNotEmpty()) {
            logger.f("Could not enable ${feature.name}, missing dependencies: ${unmetPluginDeps.joinToString()}")
            return@actions
        }

        "Enabled ${feature.name}" {
            val featureScope = FeatureCreate(scopes.getValue(feature))
            feature.onEnable(featureScope)
            enabledFeatures[feature] = featureScope
        }.onFailure(Throwable::printStackTrace)
    }

    fun disable(feature: Feature) = actions(logger) {
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

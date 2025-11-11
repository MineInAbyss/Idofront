package com.mineinabyss.idofront.features

import co.touchlab.kermit.Logger
import com.mineinabyss.idofront.commands.brigadier.Args
import com.mineinabyss.idofront.commands.brigadier.commands
import com.mineinabyss.idofront.commands.brigadier.oneOf
import com.mineinabyss.idofront.messaging.ComponentLogger
import com.mineinabyss.idofront.messaging.error
import com.mineinabyss.idofront.messaging.success
import com.mineinabyss.idofront.plugin.Plugins
import org.bukkit.plugin.Plugin
import org.koin.core.error.InstanceCreationException
import org.koin.core.error.NoDefinitionFoundException
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
    private val application = koinApplication(createEagerInstances = false) {
        // Set up global context
        modules(module(createdAtStart = true) {
            if (mainCommand != null) single<MainCommand> { mainCommand }
            single<FeatureManager> { this@FeatureManager }
            single<Plugin> { plugin }
            single { this@FeatureManager.logger } binds arrayOf(ComponentLogger::class, Logger::class)
            globalModule()
        })
    }

    private val loadedFeatures = mutableListOf<Feature>()
    private val loadedScopes = mutableMapOf<Feature, Scope>() //TODO just use koin's scopes directly?
    private val enabledFeatures = mutableMapOf<Feature, FeatureCreate>()

    private val mainFeature: Feature = feature("main") {
        onLoad {
            plugin.commands {
                val main = get<MainCommand>()
                main.names.invoke {
                    description = main.description
                    permission = main.permission
                    main.subcommands.forEach { subcommand -> subcommand(application.koin, this) }

                    if (main.reloadCommandName != null) {
                        main.reloadCommandName {
                            permission = main.reloadCommandPermission

                            executes {
                                reload()
                            }

                            executes.args(
                                "feature" to Args.string().oneOf { loadedFeatures.map { it.name }.toList() }
                            ) { featureName ->
                                if (reload(getFeature(featureName) ?: fail("Feature $featureName not found"))) {
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

    private val installedFeatures = (installed + mainFeature)
    private val dependenciesMet = mutableListOf<Feature>()

    fun filterDependenciesMet(features: List<Feature>): List<Feature> {
        return features.filter { feature ->
            val unmetPluginDeps = feature.dependencies.plugins.filterNot(Plugins::isEnabled)
            if (unmetPluginDeps.isNotEmpty()) {
                logger.w { "Not loading '${feature.name}', missing dependencies: [${unmetPluginDeps.joinToString()}]" }
                return@filter false
            }
            if (!feature.dependencies.conditions(application.koin)) {
                logger.i { "Not loading '${feature.name}', conditions not met" }
                return@filter false
            }

            true
        }
    }

    fun load() {
        logger.i { "Loading features..." }

        var dependenciesMet = listOf<Feature>()
        var newFeatures = installedFeatures
        val checked = mutableSetOf<Feature>()

        while (newFeatures.isNotEmpty()) {
            val filtered = filterDependenciesMet(newFeatures - checked)
            val subFeatures = (filtered - checked).flatMap { it.subFeatures }.distinct()
            checked += newFeatures
            newFeatures = subFeatures
            dependenciesMet = (dependenciesMet + filtered).distinct()
        }

        this.dependenciesMet += dependenciesMet

        // Set up scoped context per feature
        for (feature in dependenciesMet) {
            loadFeatureModule(feature)
        }

        val scopes = dependenciesMet.associateWith { feature ->
            application.koin.createScope(feature.name, named(feature.name))
        }

        scopes.forEach { (feature, scope) ->
            scope.linkTo(*feature.dependencies.features.map { scopes.getValue(it) }.toTypedArray())
        }

        val loaded = scopes.filter { (feature, scope) ->
            runCatching { load(feature) }.onFailure { it.printStackTrace() }.isSuccess
        }

        loadedFeatures.addAll(loaded.keys)
        this@FeatureManager.loadedScopes.putAll(loaded)
    }

    fun enable() {
        loadedFeatures.toList().forEach { feature ->
            enable(feature)
        }
    }

    fun disable() {
        enabledFeatures.keys.toList().forEach { feature ->
            disable(feature)
        }
    }

    fun load(feature: Feature) {
        if (feature !in dependenciesMet) {
            logger.f("Feature ${feature.name} is not installed")
            return
        }
        runCatching {
            feature.onLoad(application.koin)
        }.onFailure {
            logger.f("Failed to load ${feature.name}")
            if (it is InstanceCreationException) {
                logger.apply { it.printCleanErrorMessage() }
            } else it.printStackTrace()
        }
    }

    fun enable(feature: Feature): Boolean {
        if (feature !in loadedFeatures) {
            logger.f("Feature ${feature.name} is not loaded")
            return false
        }
        return runCatching {
            val scope = loadedScopes.getOrPut(feature) {
                application.koin.createScope(feature.name, named(feature.name))
                    .also { it.linkTo(*feature.dependencies.features.map { loadedScopes.getValue(it) }.toTypedArray()) }
            }
            val featureScope = FeatureCreate(scope)
            enabledFeatures[feature] = featureScope
            feature.onEnable(featureScope)
        }.onSuccess {
            logger.s("Enabled ${feature.name}")
        }.onFailure {
            logger.f("Failed to enable ${feature.name}")
            if (it is InstanceCreationException) {
                logger.apply { it.printCleanErrorMessage() }
            } else it.printStackTrace()
            disable(feature)
        }.isSuccess
    }

    fun disable(feature: Feature): Boolean {
        if (feature !in enabledFeatures.keys) {
            logger.f("Feature ${feature.name} is already disabled")
            return false
        }
        return runCatching {
            val featureScope = enabledFeatures.getValue(feature)
            feature.onDisable(featureScope)
            enabledFeatures.remove(feature)
            loadedScopes.remove(feature)
            featureScope.close()
        }.onSuccess { logger.s("Disabled ${feature.name}") }
            .onFailure {
                logger.e { "Could not disable ${feature.name}" }
                it.printStackTrace()
            }.isSuccess
    }

    fun reload(feature: Feature): Boolean {
        disable(feature)
        return enable(feature)
    }

    fun reload() {
        disable()
        enable()
    }

    fun getFeature(name: String): Feature? = loadedFeatures.find { it.name == name }

    fun getScope(feature: Feature) = loadedScopes[feature] ?: error("Cannot get scope of '${feature.name}', it is not enabled")

    inline fun <reified T : Any> get(feature: Feature): T? {
        return getScope(feature).getOrNull<T>()
    }

    private fun loadFeatureModule(feature: Feature) {
        try {
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
        } catch (e: InstanceCreationException) {
            logger.e { "Failed to create global feature module:" }
            logger.apply { e.printCleanErrorMessage() }
        }
    }
}

context(logger: Logger)
fun InstanceCreationException.printCleanErrorMessage(
    first: Boolean = true,
) {
    if (first) logger.e { "$message" }
    else logger.e { "Caused by: $message" }

    when (val cause = cause) {
        is InstanceCreationException -> cause.printCleanErrorMessage(false)
        is NoDefinitionFoundException -> logger.e { "Caused by: " + cause.message }
        else -> logger.e { "Caused by: " + cause?.stackTraceToString() }
    }
}
package com.mineinabyss.idofront.features

import co.touchlab.kermit.Logger
import com.mineinabyss.idofront.plugin.Plugins
import org.kodein.di.*

//context(koinModule: Module, plugin: Plugin)
//fun singleFeatureManager(setup: FeatureManagerBuilder.() -> Unit = {}) {
//    koinModule.single { FeatureManagerBuilder(plugin).apply(setup).build(getKoin()) }
//}

class FeatureManager(val rootDI: DI) {
    private val loadedFeatures = mutableSetOf<Feature<*>>()
    private val dependencies = mutableMapOf<Feature<*>, MutableSet<Feature<*>>>()
    private val enabledFeatures = mutableMapOf<Feature<*>, FeatureInstance>()
    private val logger = rootDI.direct.instanceOrNull<Logger>() ?: Logger
    private val defaultModule = DI.Module("default") {
        bindSingletonOf(::FeatureContext)
    }

    fun load(feature: Feature<*>) {
        if (feature in loadedFeatures) return
        if (!dependenciesMet(feature)) return

        feature.dependencies.features.forEach {
            dependencies.getOrPut(it) { mutableSetOf() }.add(feature)
        }
        feature.onLoad(rootDI.direct)
        loadedFeatures += feature
    }

    fun loadAll(vararg features: Feature<*>) {
        logger.i { "Loading features..." }
        features.forEach { load(it) }
    }

    fun dependenciesMet(feature: Feature<*>): Boolean {
        val unmetPluginDeps = feature.dependencies.plugins.filterNot(Plugins::isEnabled)
        if (unmetPluginDeps.isNotEmpty()) {
            logger.w { "Not loading '${feature.name}', missing dependencies: [${unmetPluginDeps.joinToString()}]" }
            return false
        }
        val unmetConditions = feature.dependencies.conditions.filter { !it.predicate(rootDI.direct) }
        if (unmetConditions.isNotEmpty()) {
            logger.i { "Not loading '${feature.name}', conditions not met: [${unmetConditions.joinToString()}]" }
            return false
        }

        return true
    }

    fun filterDependenciesMet(features: List<Feature<*>>): List<Feature<*>> {
        return features.filter { feature -> dependenciesMet(feature) }
    }

    fun enable(feature: Feature<*>): FeatureInstance {
        if (feature in enabledFeatures) return enabledFeatures.getValue(feature)
//        if (!feature.canEnable()) error("Could not enable $feature")
        val di = DI {
            feature.dependencies.features.forEach {
                val enabled = enabledFeatures[it] ?: enable(it)
                extend(enabled.di, allowOverride = true)
            }
            extend(rootDI, allowOverride = true)
            import(defaultModule, allowOverride = true)
            feature.diBuilder(this)
        }
        val instance = FeatureInstance(di)
        enabledFeatures[feature] = instance
        return instance
    }

    fun disable(feature: Feature<*>): List<Feature<*>> {
        if (feature !in enabledFeatures) return emptyList()
        val children = dependencies[feature]?.flatMap { disable(it) } ?: emptyList()
        enabledFeatures.remove(feature)?.close()
        return (children + feature).distinct()
    }

    fun reload(feature: Feature<*>) {
        val disabled = disable(feature)
        disabled.reversed().forEach { enable(it) }
    }

    fun enableAll() {
        loadedFeatures.toList().forEach { enable(it) }
    }

    fun disableAll() {
        enabledFeatures.keys.toList().forEach { disable(it) }
    }

    fun reloadAll() {
        disableAll()
        enableAll()
    }

    fun getInstance(feature: Feature<*>): FeatureInstance? = enabledFeatures[feature]

    fun <T : Any> get(feature: Feature<T>): T = getInstance(feature)?.di?.direct?.let { feature.extract(it) } ?: error("Feature $feature was not loaded")
    inline fun <reified T : Any> getScoped(feature: Feature<*>): T? = getInstance(feature)?.di?.direct?.let { it.instance<T>() }
}


//class FeatureManager(
//    val plugin: Plugin,
//    val koin: Koin,
//    globalModule: Module.() -> Unit,
//    installed: List<Feature<*>>,
//    mainCommand: MainCommand?,
//) {
//    //TODO inject if not provided in koin
////    private val module = module(createdAtStart = true) {
////        if (mainCommand != null) single<MainCommand> { mainCommand }
////        single<FeatureManager> { this@FeatureManager }
////        single<Plugin> { plugin }
////        single { plugin.injectedLogger() } binds arrayOf(ComponentLogger::class, Logger::class)
////        globalModule()
////    }
//    val logger get() = koin.get<ComponentLogger>()
//
//    private val loadedFeatures = mutableListOf<Feature<*>>()
//    private val loadedScopes = mutableMapOf<Feature<*>, Scope>() //TODO just use koin's scopes directly?
//    private val enabledFeatures = mutableMapOf<Feature<*>, FeatureCreate>()
//
//    private val mainFeature: Feature<Unit> = feature("main") {
//        onLoad {
//            plugin.commands {
//                val main = get<MainCommand>()
//                main.names.invoke {
//                    description = main.description
//                    permission = main.permission
//                    main.subcommands.forEach { subcommand -> subcommand(koin, this) }
//
//                    if (main.reloadCommandName != null) {
//                        main.reloadCommandName {
//                            permission = main.reloadCommandPermission
//
//                            executes {
//                                reload()
//                            }
//
//                            executes.args(
//                                "feature" to Args.string().oneOf { loadedFeatures.map { it.name }.toList() }
//                            ) { featureName ->
//                                if (reload(getFeature(featureName) ?: fail("Feature $featureName not found"))) {
//                                    sender.success("Reloaded feature $featureName")
//                                } else {
//                                    sender.error("Failed to reload feature $featureName")
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private val installedFeatures = (installed + mainFeature)
//    private val dependenciesMet = mutableListOf<Feature<*>>()
//
//    fun filterDependenciesMet(features: List<Feature<*>>): List<Feature<*>> {
//        return features.filter { feature ->
//            val unmetPluginDeps = feature.dependencies.plugins.filterNot(Plugins::isEnabled)
//            if (unmetPluginDeps.isNotEmpty()) {
//                logger.w { "Not loading '${feature.name}', missing dependencies: [${unmetPluginDeps.joinToString()}]" }
//                return@filter false
//            }
//            val unmetConditions = feature.dependencies.conditions.filter { !it.predicate(koin) }
//            if (unmetConditions.isNotEmpty()) {
//                logger.i { "Not loading '${feature.name}', conditions not met: [${unmetConditions.joinToString()}]" }
//                return@filter false
//            }
//
//            true
//        }
//    }
//
//    fun load() {
//        logger.i { "Loading features..." }
//
//        var dependenciesMet = listOf<Feature<*>>()
//        var newFeatures = installedFeatures
//        val checked = mutableSetOf<Feature<*>>()
//
//        while (newFeatures.isNotEmpty()) {
//            val filtered = filterDependenciesMet(newFeatures - checked)
//            val subFeatures = (filtered - checked).flatMap { it.subFeatures }.distinct()
//            checked += newFeatures
//            newFeatures = subFeatures
//            dependenciesMet = (dependenciesMet + filtered).distinct()
//        }
//
//        this.dependenciesMet += dependenciesMet
//
//        // Set up scoped context per feature
//        for (feature in dependenciesMet) {
//            loadFeatureModule(feature)
//        }
//
//        val scopes = dependenciesMet.associateWith { feature ->
//            koin.createScope(feature.name, named(feature.name))
//        }
//
//        scopes.forEach { (feature, scope) ->
//            scope.linkTo(*feature.dependencies.features.map { scopes.getValue(it) }.toTypedArray())
//        }
//
//        val loaded = scopes.filter { (feature, scope) ->
//            runCatching { load(feature) }.onFailure { it.printStackTrace() }.isSuccess
//        }
//
//        loadedFeatures.addAll(loaded.keys)
//        this@FeatureManager.loadedScopes.putAll(loaded)
//    }
//
//    fun enable() {
//        loadedFeatures.toList().forEach { feature ->
//            enable(feature)
//        }
//    }
//
//    fun disable() {
//        enabledFeatures.keys.reversed().forEach { feature ->
//            disable(feature)
//        }
//    }
//
//    fun load(feature: Feature<*>) {
//        if (feature !in dependenciesMet) {
//            logger.f("Feature ${feature.name} is not installed")
//            return
//        }
//        runCatching {
//            feature.onLoad(koin)
//        }.onFailure {
//            logger.f("Failed to load ${feature.name}")
//            if (it is InstanceCreationException) {
//                logger.apply { it.printCleanErrorMessage() }
//            } else it.printStackTrace()
//        }
//    }
//
//    fun enable(feature: Feature<*>): Boolean {
//        if (feature !in loadedFeatures) {
//            logger.f("Feature ${feature.name} is not loaded")
//            return false
//        }
//        return runCatching {
//            val scope = loadedScopes.getOrPut(feature) {
//                koin.createScope(feature.name, named(feature.name))
//                    .also { it.linkTo(*feature.dependencies.features.map { loadedScopes.getValue(it) }.toTypedArray()) }
//            }
//            val featureScope = FeatureCreate(scope)
//            enabledFeatures[feature] = featureScope
//            feature.onEnable(featureScope)
//        }.onSuccess {
//            logger.s("Enabled ${feature.name}")
//        }.onFailure {
//            logger.f("Failed to enable ${feature.name}")
//            if (it is InstanceCreationException) {
//                logger.apply { it.printCleanErrorMessage() }
//            } else it.printStackTrace()
//            disable(feature)
//        }.isSuccess
//    }
//
//    fun disable(feature: Feature<*>): Boolean {
//        if (feature !in enabledFeatures.keys) {
//            logger.f("Feature ${feature.name} is already disabled")
//            return false
//        }
//        return runCatching {
//            val featureScope = enabledFeatures.getValue(feature)
//            feature.onDisable(featureScope)
//            enabledFeatures.remove(feature)
//            loadedScopes.remove(feature)
//            featureScope.close()
//            koin.deleteScope(feature.name)
//        }.onSuccess { logger.s("Disabled ${feature.name}") }
//            .onFailure {
//                logger.e { "Could not disable ${feature.name}" }
//                it.printStackTrace()
//            }.isSuccess
//    }
//
//    fun reload(feature: Feature<*>): Boolean {
//        disable(feature)
//        return enable(feature)
//    }
//
//    fun reload() {
//        disable()
//        enable()
//    }
//
//    fun getFeature(name: String): Feature<*>? = loadedFeatures.find { it.name == name }
//
//    fun getScope(feature: Feature<*>) = loadedScopes[feature] ?: error("Cannot get scope of '${feature.name}', it is not enabled")
//
//    inline fun <reified T : Any> getScoped(feature: Feature<*>): T {
//        return getScope(feature).get<T>()
//    }
//
//    fun <T : Any> get(feature: Feature<T>): T {
//        return getScope(feature).get(feature.type)
//    }
//
//    private fun loadFeatureModule(feature: Feature<*>) {
//        try {
//            koin.loadModules(listOf(module {
//                runCatching {
//                    feature.globalModule(this)
//                }.onFailure {
//                    logger.e { "Error while creating global module for feature ${feature.name}" }
//                    it.printStackTrace()
//                }
//
//                scope(named(feature.name)) {
//                    runCatching {
//                        feature.scopedModule(this)
//                    }.onFailure {
//                        logger.e { "Error while creating scoped module for feature ${feature.name}" }
//                        it.printStackTrace()
//                    }
//                }
//            }))
//        } catch (e: InstanceCreationException) {
//            logger.e { "Failed to create global feature module:" }
//            logger.apply { e.printCleanErrorMessage() }
//        }
//    }
//}

//context(logger: Logger)
//fun InstanceCreationException.printCleanErrorMessage(
//    first: Boolean = true,
//) {
//    if (first) logger.e { "$message" }
//    else logger.e { "Caused by: $message" }
//
//    when (val cause = cause) {
//        is InstanceCreationException -> cause.printCleanErrorMessage(false)
//        is NoDefinitionFoundException -> logger.e { "Caused by: " + cause.message }
//        else -> logger.e { "Caused by: " + cause?.stackTraceToString() }
//    }
//}
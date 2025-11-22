package com.mineinabyss.idofront.features

import com.mineinabyss.idofront.commands.brigadier.IdoRootCommand
import com.mineinabyss.idofront.commands.brigadier.RootIdoCommands
import com.mineinabyss.idofront.commands.brigadier.commands
import com.mineinabyss.idofront.commands.brigadier.context.IdoCommandContext
import com.mineinabyss.idofront.config.ConfigBuilder
import com.mineinabyss.idofront.config.config
import org.bukkit.plugin.Plugin
import org.koin.core.Koin
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.scope.Scope
import org.koin.dsl.ScopeDSL
import kotlin.io.path.div
import kotlin.reflect.KClass

data class LoadPredicate(val reason: String, val predicate: Koin.() -> Boolean)

class FeatureBuilder(
    val name: String,
    val type: KClass<out Any>,
) : FeatureDSL {
    private var dependencies = FeatureDependencies(listOf(), listOf(), listOf())
    private var globalModule: Module.() -> Unit = {}
    private var scopedModule: ScopeDSL.() -> Unit = {}
    private var onEnable: MutableList<FeatureCreate.() -> Unit> = mutableListOf()
    private var onDisable: MutableList<FeatureCreate.() -> Unit> = mutableListOf()
    private val onLoad: MutableList<Koin.() -> Unit> = mutableListOf()
    private val subFeatures = mutableSetOf<Feature<*>>()

    class FeatureDependenciesBuilder() {
        private val features = mutableListOf<Feature<*>>()
        private val plugins = mutableListOf<String>()
        private val conditions = mutableListOf<LoadPredicate>()

        fun features(vararg feature: Feature<*>) {
            features += feature
        }

        fun plugins(vararg plugins: String) {
            this.plugins += plugins
        }

        fun condition(
            reason: String = "Conditions not met",
            predicate: Koin.() -> Boolean,
        ) {
            conditions += LoadPredicate(reason, predicate)
        }

        fun build() = FeatureDependencies(
            features = features.toList(),
            plugins = plugins.toList(),
            conditions = conditions.toList(),
        )
    }

    fun dependsOn(block: FeatureDependenciesBuilder.() -> Unit) {
        dependencies = FeatureDependenciesBuilder().apply(block).build()
    }

    fun install(vararg features: Feature<*>) {
        subFeatures += features
    }

    fun globalModule(block: Module.() -> Unit) {
        globalModule = block
    }

    //TODO disabling feature should close any AutoCloseable scoped entries
    fun scopedModule(block: ScopeDSL.() -> Unit) {
        scopedModule = block
    }

    /**
     * Injects a single serializable config of type [T], located at [path] relative to the plugin's data folder.
     *
     * For more complicated config use-cases (ex. reading a directory), use [ConfigBuilder] and manually inject via a context class.
     */
    context(scopeDsl: ScopeDSL)
    inline fun <reified T> scopedConfig(
        path: String,
        crossinline configure: context(Scope) ConfigBuilder<T>.() -> Unit = {},
    ): KoinDefinition<T> {
        return scopeDsl.scoped<T> { config<T> { configure() }.single(plugin.dataPath / path).read() }
    }

    fun commands(block: context(Koin) RootIdoCommands.() -> Unit) {
        onLoad {
            get<Plugin>().commands {
                block(this@onLoad, this)
            }
        }
    }

    fun mainCommand(block: context(Koin) IdoRootCommand.() -> Unit) {
        onLoad {
            get<MainCommand>().subcommand(block)
        }
    }

    fun onLoad(block: Koin.() -> Unit) {
        onLoad += block
    }


    fun onEnable(block: FeatureCreate.() -> Unit) {
        onEnable += block
    }

    fun onDisable(block: FeatureCreate.() -> Unit) {
        onDisable += block
    }

    context(scope: Scope)
    val plugin get() = scope.get<Plugin>()

    context(scope: Scope)
    inline fun <reified T : Any> get(): T {
        return scope.get<T>()
    }

    context(command: IdoCommandContext, koin: Koin)
    inline fun <reified T : Any> get(): T {
        val manager = featureManager
        return manager.getScope(manager.getFeature(name)!!).get<T>()
    }

    context(command: IdoCommandContext, koin: Koin)
    val featureManager: FeatureManager
        get() {
            return koin.get<FeatureManager>()
        }

    fun <T : Any> build(): Feature<T> = Feature(
        name = name,
        type = type as KClass<T>,
        dependencies = dependencies,
        globalModule = globalModule,
        subFeatures = subFeatures.toSet(),
        scopedModule = scopedModule,
        onLoad = {
            onLoad.forEach { it() }
        },
        onEnable = { this@FeatureBuilder.onEnable.forEach { it() } },
        onDisable = { this@FeatureBuilder.onDisable.forEach { it() } },
    )
}
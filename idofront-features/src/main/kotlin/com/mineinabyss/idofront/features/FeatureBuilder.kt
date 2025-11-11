package com.mineinabyss.idofront.features

import com.mineinabyss.idofront.commands.brigadier.IdoRootCommand
import com.mineinabyss.idofront.commands.brigadier.RootIdoCommands
import com.mineinabyss.idofront.commands.brigadier.commands
import com.mineinabyss.idofront.commands.brigadier.context.IdoCommandContext
import org.bukkit.plugin.Plugin
import org.koin.core.Koin
import org.koin.core.module.Module
import org.koin.core.scope.Scope
import org.koin.dsl.ScopeDSL

class FeatureBuilder(
    val name: String,
) : FeatureDSL {
    private var dependencies = FeatureDependencies(listOf(), listOf(), { true })
    private var globalModule: Module.() -> Unit = {}
    private var scopedModule: ScopeDSL.() -> Unit = {}
    private var onEnable: FeatureCreate.() -> Unit = {}
    private var onDisable: FeatureCreate.() -> Unit = {}
    private val onLoad: MutableList<Koin.() -> Unit> = mutableListOf()
    private val subFeatures = mutableSetOf<Feature>()

    class FeatureDependenciesBuilder() {
        private val features = mutableListOf<Feature>()
        private val plugins = mutableListOf<String>()
        private val conditions = mutableListOf<(Koin) -> Boolean>()

        fun features(vararg feature: Feature) {
            features += feature
        }

        fun plugins(vararg plugins: String) {
            this.plugins += plugins
        }

        fun condition(predicate: Koin.() -> Boolean) {
            conditions += predicate
        }

        fun build() = FeatureDependencies(
            features = features.toList(),
            plugins = plugins.toList(),
            conditions = { conditions.all { it(this) } }
        )
    }

    fun dependsOn(block: FeatureDependenciesBuilder.() -> Unit) {
        dependencies = FeatureDependenciesBuilder().apply(block).build()
    }

    fun install(vararg features: Feature) {
        subFeatures += features
    }

    fun globalModule(block: Module.() -> Unit) {
        globalModule = block
    }

    fun scopedModule(block: ScopeDSL.() -> Unit) {
        scopedModule = block
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
        onEnable = block
    }

    fun onDisable(block: FeatureCreate.() -> Unit) {
        onDisable = block
    }

    context(scope: Scope)
    val plugin get() = scope.get<Plugin>()

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

    fun build(): Feature = Feature(
        name = name,
        dependencies = dependencies,
        globalModule = globalModule,
        subFeatures = subFeatures.toSet(),
        scopedModule = scopedModule,
        onLoad = {
            onLoad.forEach { it() }
        },
        onEnable = onEnable,
        onDisable = onDisable,
    )
}
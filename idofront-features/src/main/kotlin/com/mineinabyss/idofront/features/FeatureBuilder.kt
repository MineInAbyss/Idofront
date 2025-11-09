package com.mineinabyss.idofront.features

import com.mineinabyss.idofront.commands.brigadier.IdoRootCommand
import com.mineinabyss.idofront.commands.brigadier.context.IdoCommandContext
import org.koin.core.Koin
import org.koin.core.module.Module
import org.koin.core.scope.Scope
import org.koin.dsl.ScopeDSL

class FeatureBuilder(
    val name: String,
) : FeatureDSL {
    private var dependencies = FeatureDependencies(listOf(), listOf())
    private var globalModule: Module.() -> Unit = {}
    private var scopedModule: ScopeDSL.() -> Unit = {}
    private var onEnable: FeatureCreate.() -> Unit = {}
    private var onDisable: FeatureCreate.() -> Unit = {}

    class FeatureDependenciesBuilder() {
        private val features = mutableListOf<Feature>()
        private val plugins = mutableListOf<String>()
        fun features(vararg feature: Feature) {
            features += feature
        }

        fun plugins(vararg plugins: String) {
            this.plugins += plugins
        }

        fun build() = FeatureDependencies(features.toList(), plugins.toList())
    }

    fun build(): Feature = Feature(
        name = name,
        dependencies = dependencies,
        globalModule = globalModule,
        scopedModule = scopedModule,
        onEnable = onEnable,
        onDisable = onDisable,
    )

    fun dependsOn(block: FeatureDependenciesBuilder.() -> Unit) {
        dependencies = FeatureDependenciesBuilder().apply(block).build()
    }

    fun globalModule(block: Module.() -> Unit) {
        globalModule = block
    }

    fun scopedModule(block: ScopeDSL.() -> Unit) {
        scopedModule = block
    }

//    context(scopeDsl: ScopeDSL)
//    inline fun <reified T> scopedConfig() {
//
//    }

    fun commands(block: context(Scope) IdoRootCommand.() -> Unit) {

    }

    fun mainCommand(block: context(Scope) IdoRootCommand.() -> Unit) {
//        global.get<MainCommand>().root.block()
        TODO()
    }

    fun onEnable(block: FeatureCreate.() -> Unit) {
        onEnable = block
    }

    fun onDisable(block: FeatureCreate.() -> Unit) {
        onDisable = block
    }

    context(command: IdoCommandContext, scope: Scope)
    inline fun <reified T: Any> get(): T {
        return scope.get<T>()
    }

    context(command: IdoCommandContext, scope: Scope)
    val featureManager: FeatureManager get() {
        return scope.get<FeatureManager>()
    }
}
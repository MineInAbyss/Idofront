package com.mineinabyss.idofront.features

import org.koin.core.Koin
import org.koin.core.module.Module
import org.koin.dsl.ScopeDSL
import kotlin.reflect.KClass

data class Feature<T : Any>(
    val type: KClass<T>,
    val name: String,
    val dependencies: FeatureDependencies,
    val subFeatures: Set<Feature<*>>,
    val globalModule: Module.() -> Unit,
    val scopedModule: ScopeDSL.() -> Unit,
    val onLoad: Koin.() -> Unit,
    val onEnable: FeatureCreate.() -> Unit,
    val onDisable: FeatureCreate.() -> Unit,
) {
    fun overrideScope(block: ScopeDSL.() -> Unit): Feature<T> {
        TODO()
    }
}

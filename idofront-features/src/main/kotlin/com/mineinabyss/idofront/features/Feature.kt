package com.mineinabyss.idofront.features

import org.koin.core.Koin
import org.koin.core.module.Module
import org.koin.dsl.ScopeDSL

data class Feature(
    val name: String,
    val dependencies: FeatureDependencies,
    val globalModule: Module.() -> Unit,
    val scopedModule: ScopeDSL.() -> Unit,
    val onLoad: Koin.() -> Unit,
    val onEnable: FeatureCreate.() -> Unit,
    val onDisable: FeatureCreate.() -> Unit,
)

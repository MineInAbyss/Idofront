package com.mineinabyss.idofront.features

import org.koin.core.Koin

data class FeatureDependencies(
    val features: List<Feature<*>>,
    val plugins: List<String>,
    val conditions: Koin.() -> Boolean,
)
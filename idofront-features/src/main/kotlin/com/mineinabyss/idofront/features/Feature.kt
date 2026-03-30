package com.mineinabyss.idofront.features

import org.kodein.di.DI
import org.kodein.di.DirectDI
import kotlin.reflect.KClass


data class Feature<T : Any>(
    val name: String,
    val type: KClass<T>, //TODO remove when updating geary, handled by extract now but needed for interop
    val dependencies: FeatureDependencies,
    val subFeatures: Set<Feature<*>>,
    val diBuilder: DI.Builder.() -> Unit,
    val extract: DirectDI.() -> T,
    val onLoad: DirectDI.() -> Unit,
) {
    fun overrideScope(block: DI.Builder.() -> Unit): Feature<T> {
        return copy(diBuilder = {
            diBuilder()
            block()
        })
    }

    override fun toString(): String = name
}

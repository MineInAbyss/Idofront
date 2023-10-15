package com.mineinabyss.idofront.features

import com.mineinabyss.idofront.di.DI
import kotlin.reflect.KClass

interface Feature {
    val dependsOn: Set<String> get() = setOf()

    fun FeatureDSL.enable() {}

    fun FeatureDSL.disable() {}

}

fun Feature.enable(plugin: FeatureDSL) = plugin.enable()
fun Feature.disable(plugin: FeatureDSL) = plugin.disable()

abstract class FeatureWithContext<T : Any>(private val contextClass: KClass<T>) : Feature {
    val context: T by DI.observe(contextClass)

    abstract fun createContext(): T

    fun createAndInjectContext(): T {
        val context = createContext()
        removeContext()
        DI.add(contextClass, context)
        return context
    }
    fun removeContext() {
        DI.remove(contextClass)
    }
}

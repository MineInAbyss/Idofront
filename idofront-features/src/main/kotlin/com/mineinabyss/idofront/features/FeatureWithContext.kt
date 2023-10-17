package com.mineinabyss.idofront.features

import com.mineinabyss.idofront.di.DI
import kotlin.reflect.KClass

abstract class FeatureWithContext<T : Any>(
    private val createContext: () -> T,
) : Feature {
    private var _contextClass: KClass<out T>? = null
    private val contextClass get() = _contextClass ?: error("Context not injected yet for $this")
    val context: T by DI.observe(contextClass)

    fun createAndInjectContext(): T {
        val context = createContext()
        _contextClass = context::class
        removeContext()
        DI.add(contextClass, context)
        return context
    }

    fun removeContext() {
        DI.remove(contextClass)
    }
}

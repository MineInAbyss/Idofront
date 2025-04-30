package com.mineinabyss.idofront.di

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * A simple pointer to a module that [DI] can update automatically.
 */
class ModuleObserver<T>(private val name: String) : ReadOnlyProperty<Any?, T> {
    internal var module: T? = null
    internal var delegate: (() -> T)? = null
    internal var delegating = false

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = get()

    fun get() = getOrNull()
        ?: error("Tried getting module '$name', but it was not registered")

    fun getOrNull() = (if (delegating) delegate?.invoke() else module)
}

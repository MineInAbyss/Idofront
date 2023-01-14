package com.mineinabyss.idofront.di

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * A simple pointer to a module that [DI] can update automatically.
 */
class ModuleObserver<T>(private val name: String) : ReadOnlyProperty<Any?, T> {
    internal var module: T? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T =
        module ?: error("Tried getting module '$name', but it was not registered")
}

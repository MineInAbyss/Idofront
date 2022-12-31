package com.mineinabyss.idofront.di

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * A simple pointer to a module that [DI] can update automatically.
 */
class ModuleObserver<T>(var module: T) : ReadOnlyProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = module
}

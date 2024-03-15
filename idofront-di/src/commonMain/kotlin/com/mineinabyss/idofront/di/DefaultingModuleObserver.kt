package com.mineinabyss.idofront.di

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class DefaultingModuleObserver<T>(
    val observer: ModuleObserver<T>,
    val defaultProvider: () -> T,
) : ReadOnlyProperty<Any?, T> {
    val default by lazy { defaultProvider() }

    fun get(): T = observer.getOrNull() ?: default

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return get()
    }
}

fun <T> ModuleObserver<T>.default(provider: () -> T): DefaultingModuleObserver<T> {
    return DefaultingModuleObserver(this, provider)
}

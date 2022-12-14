package com.mineinabyss.idofront.di

import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Main class for registering and observing dependency injection modules.
 */
object DI {
    @PublishedApi
    internal val modules = mutableMapOf<KType, Any>()

    @PublishedApi
    internal val moduleObservers = mutableMapOf<KType, ModuleObserver<Any>>()

    /**
     * Gets an observer for a module of type [T].
     *
     * If this module is ever updated, this reference will be updated as well.
     */
    inline fun <reified T> observe(): ModuleObserver<T> {
        return (moduleObservers[typeOf<T>()] ?: error("Module ${typeOf<T>()} not registered")) as ModuleObserver<T>
    }

    /** Registers a module of type [T]. */
    inline fun <reified T : Any> add(module: T) {
        modules[typeOf<T>()] = module
        moduleObservers.getOrPut(typeOf<T>()) { ModuleObserver(module) }.module = module
    }
}


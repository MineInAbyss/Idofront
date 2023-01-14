package com.mineinabyss.idofront.di

import kotlin.reflect.KClass

/**
 * An isolated context for dependency injection.
 *
 * Use [DI] as a global context.
 */
open class DIContext {
    @PublishedApi
    internal val modules = mutableMapOf<KClass<out Any>, Any>()

    @PublishedApi
    internal val moduleObservers = mutableMapOf<KClass<out Any>, ModuleObserver<out Any>>()

    /**
     * Gets an observer for a module of type [T].
     *
     * If this module is ever updated, this reference will be updated as well.
     */
    inline fun <reified T : Any> observe(): ModuleObserver<T> = observe(T::class)

    /** Registers a module of type [T]. */
    inline fun <reified T : Any> add(module: T) = add(T::class, module)

    inline fun <reified T : Any> remove() = remove(T::class)

    /** Gets a module by type directly */
    inline fun <reified T : Any> get(): T = get(T::class)

    /** Gets a module by type directly */
    inline fun <reified T : Any> getOrNull(): T? = getOrNull(T::class)

    fun <T : Any> observe(type: KClass<T>): ModuleObserver<T> = getOrPutModuleObserver(type)

    fun <T : Any> add(type: KClass<T>, module: T) {
        modules[type] = module
        getOrPutModuleObserver(type).module = module
    }

    fun <T : Any> remove(type: KClass<T>) {
        modules.remove(type)
        moduleObservers[type]?.module = null
    }

    fun <T : Any> get(type: KClass<T>): T = getOrNull(type) ?: error("Module ${type.simpleName} not registered")

    @Suppress("UNCHECKED_CAST") // Logic ensures safety
    fun <T : Any> getOrNull(type: KClass<T>): T? = modules[type] as? T

    fun clear() {
        modules.clear()
        moduleObservers.forEach { it.value.module = null }
    }

    @Suppress("UNCHECKED_CAST") // Logic ensures safety
    private fun <T : Any> getOrPutModuleObserver(type: KClass<T>): ModuleObserver<T> {
        return moduleObservers.getOrPut(type) {
            ModuleObserver<T>(type.simpleName ?: "Unknown Class")
        } as ModuleObserver<T>
    }
}

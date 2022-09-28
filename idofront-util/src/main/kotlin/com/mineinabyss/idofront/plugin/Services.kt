package com.mineinabyss.idofront.plugin

import org.bukkit.Bukkit
import org.bukkit.plugin.ServicesManager

object Services {
    /**
     * Gets a service registered with Bukkit of type [T], throws an error if not found.
     *
     * @see ServicesManager.load
     * @see getOrNull
     */
    inline fun <reified T> get(): T =
        getOrNull<T>() ?: error("Could not load service ${T::class.simpleName}")

    /**
     * Gets a service registered with Bukkit of type [T] or null if not found.
     *
     * @see ServicesManager.load
     */
    inline fun <reified T> getOrNull(): T? =
        runCatching {
            Bukkit.getServer().servicesManager.load(T::class.java)
        }.getOrNull()

    /**
     * Gets a service in a way that will ignore class differences across different classloaders.
     *
     * The returned type may then be cast to some common class that is shared between both loaders, such
     * as a Function type in Kotlin.
     */
    inline fun <reified T, C> getViaClassNameOrNull(): C? {
        val serviceManager = Bukkit.getServer().servicesManager
        val className = T::class.java.name
        val clazz = serviceManager.knownServices.first { it.name == className }
        return serviceManager.load(clazz) as? C
    }
}

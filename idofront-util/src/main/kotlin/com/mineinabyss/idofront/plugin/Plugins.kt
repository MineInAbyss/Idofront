package com.mineinabyss.idofront.plugin

import com.mineinabyss.idofront.plugin.Plugins.getOrNull
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin

object Plugins {
    /**
     * Gets a plugin registered with Bukkit with main class [T], throws an error if not found.
     * @see getOrNull
     */
    inline fun <reified T : Plugin> get(): T =
        getOrNull() ?: error("Could not find plugin ${T::class.simpleName}")

    /**
     * Gets a plugin by name, ensuring it's of type [T], throws an error if not found or class isn't loaded.
     */
    inline fun <reified T> get(name: String): T =
        getOrNull(name) ?: error("Could not find plugin ${T::class.simpleName}")

    /** Gets a plugin registered with Bukkit with main class [T] or null if not found. */
    inline fun <reified T : Plugin> getOrNull(): T? {
        if (runCatching { T::class }.isFailure) return null
        return Bukkit.getPluginManager().plugins.find { it::class == T::class } as? T
    }

    /**
     * Gets a plugin by name, ensuring it's of type [T].
     *
     * If the class [T] is not loaded, returns `null`.
     */
    inline fun <reified T> getOrNull(name: String): T? {
        if (runCatching { T::class }.isFailure) return null
        return Bukkit.getPluginManager().getPlugin(name) as? T
    }

    /** Checks if a plugin with main class [T] exists and is enabled. */
    inline fun <reified T : Plugin> isEnabled(): Boolean =
        getOrNull<T>()?.let { isEnabled(it) } ?: false

    /** Checks if a [plugin] exists and is enabled. */
    fun isEnabled(plugin: Plugin) = runCatching { Bukkit.getPluginManager().isPluginEnabled(plugin) }.getOrDefault(false)

    /** Checks if a [plugin] exists and is enabled. */
    fun isEnabled(plugin: String) = Bukkit.getPluginManager().isPluginEnabled(plugin)

    /** Checks if a plugin with main class [T] exists. */
    inline fun <reified T : Plugin> exists() = getOrNull<T>() != null
}

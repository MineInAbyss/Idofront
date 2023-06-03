package com.mineinabyss.idofront.plugin

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin

object Plugins {
    /**
     * Gets a plugin registered with Bukkit with main class [T], throws an error if not found.
     * @see getOrNull
     */
    inline fun <reified T : Plugin> get(): T =
        getOrNull() ?: error("Could not find plugin ${T::class.simpleName}")

    /** Gets a plugin registered with Bukkit with main class [T] or null if not found. */
    inline fun <reified T : Plugin> getOrNull(): T? {
        if (runCatching { T::class }.isFailure) return null
        return Bukkit.getPluginManager().plugins.find { it::class == T::class } as? T
    }

    /** Checks if a plugin with main class [T] exists and is enabled. */
    inline fun <reified T : Plugin> isEnabled(): Boolean =
        getOrNull<T>()?.let { isEnabled(it) } ?: false

    /** Checks if a [plugin] exists and is enabled. */
    fun isEnabled(plugin: Plugin) = runCatching { Bukkit.getPluginManager().isPluginEnabled(plugin) }.isSuccess

    /** Checks if a [plugin] exists and is enabled. */
    fun isEnabled(plugin: String) = Bukkit.getPluginManager().isPluginEnabled(plugin)

    /** Checks if a plugin with main class [T] exists. */
    inline fun <reified T : Plugin> exists() = getOrNull<T>() != null
}

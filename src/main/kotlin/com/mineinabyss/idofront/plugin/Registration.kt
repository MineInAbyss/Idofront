package com.mineinabyss.idofront.plugin

import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.ServicesManager


/**
 * Registers a Bukkit service of type [T] with an implementation [impl] and [priority].
 *
 * @see ServicesManager.register
 */
inline fun <reified T : Any> Plugin.registerService(impl: T, priority: ServicePriority = ServicePriority.Lowest) =
    server.servicesManager.register(T::class.java, impl, this, priority)

/**
 * Registers a list of [listeners] with Bukkit's event system.
 *
 * @see PluginManager.registerEvents
 */
fun Plugin.registerEvents(vararg listeners: Listener) =
    listeners.forEach { server.pluginManager.registerEvents(it, this) }

/**
 * Gets a service registered with Bukkit of type [T], throws an error if not found.
 *
 * @see ServicesManager.load
 * @see getServiceOrNull
 */
inline fun <reified T> getService(): T =
    getServiceOrNull<T>(null) ?: error("Could not load service ${T::class.simpleName}")

/**
 * Gets a service registered with Bukkit of type [T] or null if not found.
 *
 * @param plugin The name of the plugin from which to get this service. This is used to check whether that plugin is
 * enabled before trying to load the service, thus preventing any exceptions.
 * @see ServicesManager.load
 */
inline fun <reified T> getServiceOrNull(plugin: String? = null): T? =
    if (plugin == null || isPluginEnabled(plugin))
        Bukkit.getServer().servicesManager.load(T::class.java)
    else null


/**
 * Gets a plugin registered with Bukkit of type [T], throws an error if not found.
 * @see getPluginOrNull
 */
inline fun <reified T : Plugin> getPlugin(): T =
    getPluginOrNull<T>(T::class.simpleName!!) ?: error("Could not find plugin ${T::class.simpleName}")

/** Gets a plugin registered with Bukkit of type [T] or null if not found. */
inline fun <reified T : Plugin> getPluginOrNull(plugin: String): T? =
    if (isPluginEnabled(plugin))
        Bukkit.getPluginManager().getPlugin(plugin) as? T
    else null

inline fun isPluginEnabled(plugin: String) = Bukkit.getServer().pluginManager.isPluginEnabled(plugin)

inline fun doesPluginExist(plugin: String) = Bukkit.getServer().pluginManager.getPlugin(plugin) != null

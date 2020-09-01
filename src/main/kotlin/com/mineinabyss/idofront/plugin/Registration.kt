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
inline fun <reified T> Plugin.registerService(impl: T, priority: ServicePriority = ServicePriority.Lowest) =
    server.servicesManager.register(T::class.java, impl, this, priority)

/**
 * Registers a list of [listeners] with Bukkit's event system.
 *
 * @see PluginManager.registerEvents
 */
fun Plugin.registerEvents(vararg listeners: Listener) =
    listeners.forEach { server.pluginManager.registerEvents(it, this) }

/**
 * Gets a service registered with Bukkit of type [T]
 *
 * @see ServicesManager.load
 */
inline fun <reified T> getService() =
    Bukkit.getServer().servicesManager.load(T::class.java)
        ?: error("Could not load service for ${T::class.simpleName}")

/** Gets a plugin registered with Bukkit of type [T] */
inline fun <reified T : Plugin> getPlugin() = Bukkit.getPluginManager().plugins.first { it is T }

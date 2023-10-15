package com.mineinabyss.idofront.plugin

import org.bukkit.event.HandlerList
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
inline fun <reified T : Any> Plugin.service(impl: T, priority: ServicePriority = ServicePriority.Lowest) =
    server.servicesManager.register(T::class.java, impl, this, priority)

/**
 * Registers a list of [listeners] with Bukkit's event system.
 *
 * @see PluginManager.registerEvents
 */
fun Plugin.listeners(vararg listeners: Listener) =
    listeners.forEach { server.pluginManager.registerEvents(it, this) }

/** Unregisters any handlers from the passed [listeners]. */
fun Plugin.unregisterListeners(vararg listeners: Listener) =
    listeners.forEach { HandlerList.unregisterAll(it) }


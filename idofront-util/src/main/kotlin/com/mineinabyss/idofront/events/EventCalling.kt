package com.mineinabyss.idofront.events

import org.bukkit.Bukkit
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.plugin.PluginManager

/**
 * Calls this event
 *
 * @see [PluginManager.callEvent]
 */
fun Event.call() = Bukkit.getServer().pluginManager.callEvent(this)

/**
 * Calls this event and runs [onSuccess] if the event isn't [cancelled][Cancellable.isCancelled] after the call
 * is finished.
 *
 * @see [PluginManager.callEvent]
 */
inline fun Event.call(onSuccess: Event.() -> Unit) {
    Bukkit.getServer().pluginManager.callEvent(this)
    if (this !is Cancellable || !isCancelled)
        onSuccess()
}

/**
 * Calls this event and runs [onSuccess] if the event is an instance of [T] and isn't [cancelled][Cancellable.isCancelled] after the call
 * is finished.
 *
 * @see [PluginManager.callEvent]
 */
@JvmName("callCast")
inline fun <reified T : Event> Event.call(onSuccess: T.() -> Unit) {
    if (this !is T) return
    Bukkit.getServer().pluginManager.callEvent(this)
    if (this !is Cancellable || !isCancelled)
        onSuccess()
}

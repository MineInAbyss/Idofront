package com.mineinabyss.idofront.events

import org.bukkit.Bukkit
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
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
 * Mostly just so we can return a cancelled without needing so many lines
 * if (x) {
 *  isCancelled = true
 *  return
 * }
 * to
 * return cancelIf(x)
 */
fun Cancellable.cancelIf(condition: Boolean) {
    if (condition) isCancelled = true
}

fun Cancellable.cancelIf(condition: () -> Boolean) {
    if (condition()) isCancelled = true
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

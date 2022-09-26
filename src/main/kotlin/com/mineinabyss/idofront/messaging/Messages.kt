@file:JvmName("Messages")

package com.mineinabyss.idofront.messaging

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender

@PublishedApi
internal val mm = MiniMessage.miniMessage()
internal val plain = PlainTextComponentSerializer.plainText()
private const val ERROR_PREFIX = "<dark_red><b>\u274C</b><red>"
private const val SUCCESS_PREFIX = "<green><b>\u2714</b>"
private const val WARN_PREFIX = "<yellow>\u26A0<gray>"

/** Broadcasts a message to the entire server. */
fun broadcast(message: Any?) = logTo(message) { Bukkit.getServer().broadcast(it) }

inline fun logTo(message: Any?, printTo: (Component) -> Unit) {
    if (message is Component) printTo(message)
    else printTo(mm.deserialize("$message"))
}

fun logInfo(message: Any?) =
    logTo(message, Bukkit.getConsoleSender()::sendMessage)

fun logError(message: Any?) =
    Bukkit.getLogger().severe("$message")

fun logSuccess(message: Any?) =
    logTo("<green>$message", Bukkit.getConsoleSender()::sendMessage)

fun logWarn(message: Any?) =
    Bukkit.getLogger().warning("$message")

fun CommandSender.info(message: Any?) = logTo(message, ::sendMessage)

fun CommandSender.error(message: Any?) {
    if (this is ConsoleCommandSender) logError(message)
    else info("$ERROR_PREFIX $message")
}

fun CommandSender.success(message: Any?) {
    if (this is ConsoleCommandSender) logSuccess(message)
    else info("$SUCCESS_PREFIX $message")
}

fun CommandSender.warn(message: Any?) {
    if (this is ConsoleCommandSender) logWarn(message)
    else info("$WARN_PREFIX $message")
}

/** Parses this String to a [Component] with MiniMessage and an optional TagResolver */
fun String.miniMsg(tagResolver: TagResolver = TagResolver.standard()): Component = mm.deserialize(this, tagResolver)

/** Serializes this [Component] to a String with MiniMessage */
fun Component.serialize(): String = mm.serialize(this)

/** Serializes this [Component] to a plain text string */
fun Component.toPlainText(): String = plain.serialize(this)

/** Removes all supported tags from a string, with an optional TagResolver input */
fun String.stripTags(tagResolver: TagResolver = TagResolver.standard()): String = mm.stripTags(this, tagResolver)

/**
 * (Kotlin) Logs a value with an optional string in front of it e.x.
 *
 * ```
 * val length: Int = "A String".logVal("Name").length.logVal("Its length")
 * ```
 * Will print:
 * ```
 * Name: A String
 * Its length: 8
 * ```
 *
 * @param message A string to be placed in front of this value.
 * @return Itself.
 */
fun <T> T.logVal(message: String = ""): T = logInfo("${if (message == "") "" else "$message: "}$this").let { this }

/**
 * Same as [logVal] but uses [broadcast] instead
 */
fun <T> T.broadcastVal(message: String = ""): T = broadcast("$message$this").let { this }

@file:JvmName("Messages")

package com.mineinabyss.idofront.messaging

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender

/**
 * Same as [logVal] but uses [broadcast] instead
 */
fun <T> T.broadcastVal(message: String = ""): T = broadcast("$message$this").let { this }

/**
 * (Kotlin) Logs a value with an optional string in front of it e.x.
 *
 * ```
 * val length: Int = "A String".logVal("Name: ").length.logVal("Its length: ")
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
fun <T> T.logVal(message: String = ""): T = logInfo("$message${if (message == "") " " else ""}$this").let { this }

/**
 * Runs [Bukkit.broadcastMessage].
 */
fun broadcast(message: String) = Bukkit.broadcastMessage(message)


inline fun logTo(message: String, colorChar: Char? = null, printTo: (String) -> Unit) =
        printTo(message.color(colorChar))

fun logInfo(message: String, colorChar: Char? = null) =
        logTo(message, colorChar, Bukkit.getLogger()::info)

fun logError(message: String, colorChar: Char? = null) =
        logTo(message, colorChar, Bukkit.getLogger()::severe)

fun logSuccess(message: String, colorChar: Char? = null) =
        logTo("${ChatColor.GREEN}$message", colorChar, Bukkit.getLogger()::info)

fun logWarn(message: String, colorChar: Char? = null) =
        logTo(message, colorChar, Bukkit.getLogger()::warning)

private val ERROR_PREFIX = "&4&l\u274C&c".color()
private val SUCCESS_PREFIX = "&a&l\u2714&a".color()
private val WARN_PREFIX = "&e\u26A0&7".color()

fun CommandSender.info(message: String, colorChar: Char? = null) = logTo(message, colorChar, ::sendMessage)

fun CommandSender.error(message: String, colorChar: Char? = null) {
    if (this is ConsoleCommandSender) logError(message, colorChar)
    else info("$ERROR_PREFIX $message", colorChar)
}

fun CommandSender.success(message: String, colorChar: Char? = null) {
    if (this is ConsoleCommandSender) logSuccess(message, colorChar)
    else info("$SUCCESS_PREFIX $message", colorChar)
}

fun CommandSender.warn(message: String, colorChar: Char? = null) {
    if (this is ConsoleCommandSender) logWarn(message, colorChar)
    else info("$WARN_PREFIX $message", colorChar)
}

/**
 * Translates a string using Minecraft color codes with [ChatColor.translateAlternateColorCodes]
 */
@JvmOverloads
fun String.color(colorChar: Char? = '&') = apply {
    if (colorChar != null)
        return ChatColor.translateAlternateColorCodes(colorChar, this)
}

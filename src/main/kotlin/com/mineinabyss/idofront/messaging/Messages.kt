@file:JvmName("Messages")

package com.mineinabyss.idofront.messaging

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

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
fun <T> T.logVal(message: String = ""): T = logInfo("$message$this").let { this }

/**
 * Runs [Bukkit.broadcastMessage].
 */
fun broadcast(message: String) = Bukkit.broadcastMessage(message)

fun logInfo(message: String, colorChar: Char? = null, color: ChatColor = ChatColor.WHITE) {
    Bukkit.getLogger().info(message.translateAndColor(color, colorChar))
}

fun logError(message: String, colorChar: Char? = null) = logInfo(message, colorChar, ChatColor.RED)
fun logSuccess(message: String, colorChar: Char? = null) = logInfo(message, colorChar, ChatColor.GREEN)
fun logWarn(message: String, colorChar: Char? = null) = logInfo(message, colorChar, ChatColor.YELLOW)

@JvmOverloads
fun CommandSender.info(message: String, colorChar: Char? = null, color: ChatColor = ChatColor.WHITE) {
    sendMessage(message.translateAndColor(color, colorChar))
}

@JvmOverloads
fun CommandSender.error(message: String, colorChar: Char? = null) = info(message, colorChar, ChatColor.RED)

@JvmOverloads
fun CommandSender.success(message: String, colorChar: Char? = null) = info(message, colorChar, ChatColor.GREEN)

/**
 * Translates a string using Minecraft color codes with [ChatColor.translateAlternateColorCodes]
 */
@JvmOverloads
fun String.color(colorChar: Char = '&') = ChatColor.translateAlternateColorCodes(colorChar, this)

private fun String.translateAndColor(color: ChatColor, colorChar: Char?): String {
    val translatedMessage = if (colorChar != null) this.color(colorChar) else this
    return "$color$translatedMessage"
}
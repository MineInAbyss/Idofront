@file:JvmName("Messages")
package com.mineinabyss.idofront

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

fun logInfo(message: String, color: ChatColor = ChatColor.WHITE) = Bukkit.getLogger().info("$color$message")

fun logError(message: String) = logInfo(message, ChatColor.RED)
fun logSuccess(message: String) = logInfo(message, ChatColor.GREEN)
fun logWarn(message: String) = logInfo(message, ChatColor.YELLOW)

@JvmOverloads
fun CommandSender.info(message: String, color: ChatColor = ChatColor.WHITE, colorChar: Char? = null) =
        sendMessage(if (colorChar != null) message.translateColors(colorChar) else message)

@JvmOverloads
fun CommandSender.error(message: String, colorChar: Char? = null) = info(message, ChatColor.RED, colorChar)

@JvmOverloads
fun CommandSender.success(message: String, colorChar: Char? = null) = info(message, ChatColor.GREEN, colorChar)

/**
 * Translates a string using Minecraft color codes with [ChatColor.translateAlternateColorCodes]
 */
@JvmOverloads
fun String.translateColors(colorChar: Char = '&') = ChatColor.translateAlternateColorCodes(colorChar, this)
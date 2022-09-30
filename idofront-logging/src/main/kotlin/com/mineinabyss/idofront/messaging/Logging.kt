package com.mineinabyss.idofront.messaging

import com.mineinabyss.idofront.textcomponents.miniMsg
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.plugin.java.PluginClassLoader

@PublishedApi
internal object Logging {
    // Get plugin via classloader
    val pluginPrefix = (Logging::class.java.classLoader as? PluginClassLoader)?.plugin?.name?.let { "[$it] ".miniMsg() } ?: Component.empty()
}

private const val ERROR_PREFIX = "<dark_red><b>\u274C</b><red>"
private const val SUCCESS_PREFIX = "<green><b>\u2714</b>"
private const val WARN_PREFIX = "<yellow>\u26A0<gray>"

/** Broadcasts a message to the entire server. */
fun broadcast(message: Any?) = logTo(message) { Bukkit.getServer().broadcast(it) }

inline fun logTo(message: Any?, addPrefix: Boolean = true, printTo: (Component) -> Unit) {
    val messageComponent = message as? Component ?: message.toString().miniMsg()
    val fullMessage = if (addPrefix) Logging.pluginPrefix.append(messageComponent) else messageComponent
    printTo(fullMessage)
}

fun logInfo(message: Any?) =
    logTo(message, printTo = Bukkit.getConsoleSender()::sendMessage)

fun logError(message: Any?) =
    Bukkit.getLogger().severe("$message")

fun logSuccess(message: Any?) =
    logTo("<green>$message", printTo = Bukkit.getConsoleSender()::sendMessage)

fun logWarn(message: Any?) =
    Bukkit.getLogger().warning("$message")

fun CommandSender.info(message: Any?) = logTo(message, addPrefix = false, printTo = ::sendMessage)

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

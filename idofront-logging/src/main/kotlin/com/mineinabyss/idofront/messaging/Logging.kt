package com.mineinabyss.idofront.messaging

import com.mineinabyss.idofront.textcomponents.miniMsg
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.plugin.java.PluginClassLoader
import org.tinylog.Logger

@PublishedApi
internal object Logging {
    // Get plugin via classloader
    val pluginPrefix = runCatching {
        (Logging::class.java.classLoader as? PluginClassLoader)?.plugin?.name?.let { "[$it] ".miniMsg() }
    }.getOrNull() ?: Component.empty()
    val legacySerializer = LegacyComponentSerializer.legacySection()
}

object Prefixes {
    val ERROR_PREFIX = "<dark_red><b>\u274C</b><red>".miniMsg()
    val SUCCESS_PREFIX = "<green><b>\u2714</b>".miniMsg()
    val WARN_PREFIX = "<yellow>\u26A0<gray>".miniMsg()
}

/** Broadcasts a message to the entire server. */
fun broadcast(message: Any?) = logTo(
    message,
    printTo = { Bukkit.getServer().broadcast(it) },
    fallback = { Logger.info { it } }
)

inline fun logTo(message: Any?, addPrefix: Boolean = true, printTo: (Component) -> Unit, fallback: (String) -> Unit) {
    val messageComponent = message as? Component ?: message.toString().miniMsg()
    val fullMessage = if (addPrefix) Logging.pluginPrefix.append(messageComponent) else messageComponent
    runCatching {
        printTo(fullMessage)
    }.onFailure {
        println(ChatColor.stripColor(Logging.legacySerializer.serialize(fullMessage)))
    }
}

fun logInfo(message: Any?) = logTo(
    message,
    printTo = { Bukkit.getConsoleSender().sendMessage(it) },
    fallback = { Logger.info(message) }
)

fun logError(message: Any?) =
    Bukkit.getLogger().severe("$message")

fun logSuccess(message: Any?) = logTo(
    message,
    printTo = { Bukkit.getConsoleSender().sendMessage(it) },
    fallback = { Logger.info { it } }
)

fun logWarn(message: Any?) =
    Bukkit.getLogger().warning("$message")

fun CommandSender.info(message: Any?, prefix: Component) = logTo(
    message,
    addPrefix = false,
    printTo = {
        if (this is ConsoleCommandSender)
            sendMessage(prefix.append(it))
    },
    fallback = { Logger.info()}
)

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

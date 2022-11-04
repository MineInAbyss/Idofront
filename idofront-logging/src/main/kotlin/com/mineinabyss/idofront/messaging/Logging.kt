package com.mineinabyss.idofront.messaging

import com.mineinabyss.idofront.textcomponents.miniMsg
import com.mineinabyss.idofront.textcomponents.toPlainText
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.plugin.java.PluginClassLoader

@PublishedApi
internal object Logging {
    // Get plugin via classloader
    val pluginPrefix by lazy {
        (Logging::class.java.classLoader as? PluginClassLoader)?.plugin?.name?.let { "[$it] ".miniMsg() }
            ?: Component.empty()
    }
}

private const val ERROR_PREFIX = "<dark_red><b>\u274C</b><red>"
private const val SUCCESS_PREFIX = "<green><b>\u2714</b>"
private const val WARN_PREFIX = "<yellow>\u26A0<gray>"

@PublishedApi
internal val BUKKIT_LOADED = runCatching {
    Bukkit.getConsoleSender()
}.isSuccess

@PublishedApi
internal val ADVENTURE_LOADED = runCatching {
    "<green>Test".miniMsg()
}.isSuccess

//private val BUKKIT_OR_DEFAULT_LOGGER: (Any?) -> Unit = {
//    if (BUKKIT_LOADED) Bukkit.getConsoleSender().sendMessage(it)
//}

/** Broadcasts a message to the entire server. */
fun broadcast(message: Any?) = logTo(message) { Bukkit.getServer().broadcast(it) }

inline fun logTo(message: Any?, addPrefix: Boolean = true, printBukkit: (Component) -> Unit) {
    if (ADVENTURE_LOADED) {
        val messageComponent = message as? Component ?: message.toString().miniMsg()
        val fullMessage = if (addPrefix && BUKKIT_LOADED)
            Logging.pluginPrefix.append(messageComponent)
        else messageComponent

        if (BUKKIT_LOADED) printBukkit(fullMessage)
        else println(fullMessage.toPlainText())
    } else {
        println(message)
    }
}

fun logInfo(message: Any?) =
    logTo(message) { Bukkit.getConsoleSender().sendMessage(it) }

fun logSuccess(message: Any?) =
    logTo("<green>$message") { Bukkit.getConsoleSender().sendMessage(it) }

fun logError(message: Any?) =
    logTo(message) { Bukkit.getLogger().severe(it.toPlainText()) }

fun logWarn(message: Any?) =
    logTo(message) { Bukkit.getLogger().warning(it.toPlainText()) }

fun CommandSender.info(message: Any?) = logTo(message, addPrefix = false, printBukkit = ::sendMessage)

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

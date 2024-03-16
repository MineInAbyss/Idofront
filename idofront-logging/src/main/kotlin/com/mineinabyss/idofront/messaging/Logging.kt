package com.mineinabyss.idofront.messaging

import com.mineinabyss.idofront.messaging.IdoLogging.ERROR_PREFIX
import com.mineinabyss.idofront.messaging.IdoLogging.SUCCESS_PREFIX
import com.mineinabyss.idofront.messaging.IdoLogging.WARN_PREFIX
import com.mineinabyss.idofront.messaging.IdoLogging.logWithFallback
import com.mineinabyss.idofront.textcomponents.miniMsg
import com.mineinabyss.idofront.textcomponents.toPlainText
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender

object IdoLogging {
    const val ERROR_PREFIX = "<dark_red><b>\u274C</b><red> "
    const val SUCCESS_PREFIX = "<green><b>\u2714</b> "
    const val WARN_PREFIX = "<yellow>\u26A0<gray> "

    val successComp = SUCCESS_PREFIX.miniMsg()
    val errorComp = ERROR_PREFIX.miniMsg()
    val warnComp = WARN_PREFIX.miniMsg()

    @PublishedApi
    internal val BUKKIT_LOADED = runCatching {
        Bukkit.getConsoleSender()
    }.isSuccess

    @PublishedApi
    internal val ADVENTURE_LOADED = runCatching {
        "<green>Test".miniMsg()
    }.isSuccess

    inline fun logWithFallback(message: Any?, printBukkit: (Component) -> Unit) {
        if (ADVENTURE_LOADED) {
            val messageComponent = message as? Component ?: message.toString().miniMsg()
            if (BUKKIT_LOADED) printBukkit(messageComponent)
            else println(messageComponent.toPlainText())
        } else {
            println(message)
        }
    }
}

@Deprecated("Use Plugin.logger().i(...)")
fun logInfo(message: Any?) =
    logWithFallback(message) { Bukkit.getConsoleSender().sendMessage(it) }

@Deprecated("Use Plugin.logger().i(...)")
fun logSuccess(message: Any?) =
    logWithFallback("<green>$message") { Bukkit.getConsoleSender().sendMessage(it) }

@Deprecated("Use Plugin.logger().e(...)")
fun logError(message: Any?) =
    logWithFallback(message) { Bukkit.getLogger().severe(it.toPlainText()) }

@Deprecated("Use Plugin.logger().w(...)")
fun logWarn(message: Any?) =
    logWithFallback(message) { Bukkit.getLogger().warning(it.toPlainText()) }

/** Broadcasts a message to the entire server. */
fun broadcast(message: Any?) = logWithFallback(message) { Bukkit.getServer().broadcast(it) }

fun CommandSender.info(message: Any?) = logWithFallback(message, printBukkit = ::sendMessage)

fun CommandSender.error(message: Any?) {
    if (this is ConsoleCommandSender)
        logWithFallback(message) { Bukkit.getLogger().severe(it.toPlainText()) }
    else info("$ERROR_PREFIX$message")
}

fun CommandSender.success(message: Any?) {
    if (this is ConsoleCommandSender)
        logWithFallback("<green>$message") { Bukkit.getConsoleSender().sendMessage(it) }
    else info("$SUCCESS_PREFIX$message")
}

fun CommandSender.warn(message: Any?) {
    if (this is ConsoleCommandSender)
        logWithFallback(message) { Bukkit.getLogger().warning(it.toPlainText()) }
    else info("$WARN_PREFIX$message")
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
fun <T> T.logVal(message: String = ""): T = logWithFallback(
    "${if (message == "") "" else "$message: "}$this",
    printBukkit = Bukkit.getConsoleSender()::sendMessage
).let { this }

/**
 * Same as [logVal] but uses [broadcast] instead
 */
fun <T> T.broadcastVal(message: String = ""): T = broadcast("$message$this").let { this }

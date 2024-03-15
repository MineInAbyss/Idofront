package com.mineinabyss.idofront.messaging

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity
import com.mineinabyss.idofront.textcomponents.toPlainText
import net.kyori.adventure.text.ComponentLike
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.util.logging.Level

class KermitPaperWriter(private val plugin: Plugin) : LogWriter() {
    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        plugin.logger.log(severityToLogLevel(severity), message)
        throwable?.printStackTrace()
    }

    fun log(severity: Severity, message: ComponentLike) {
        if (severity >= Severity.Warn)
            log(severity, message.asComponent().toPlainText(), "", null)
        else
            Bukkit.getConsoleSender().sendMessage(message)
    }

    companion object {
        // Spigot passes the java log level into log4j that's harder to configure, we'll just stick to info level
        // and filter on our end
        fun severityToLogLevel(severity: Severity): Level = when (severity) {
            Severity.Verbose -> Level.INFO
            Severity.Debug -> Level.INFO
            Severity.Info -> Level.INFO
            Severity.Warn -> Level.WARNING
            Severity.Error -> Level.SEVERE
            Severity.Assert -> Level.SEVERE
        }
    }
}

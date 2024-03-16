package com.mineinabyss.idofront.messaging

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity
import com.mineinabyss.idofront.textcomponents.toPlainText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.util.logging.Level

class KermitPaperWriter(private val plugin: Plugin) : LogWriter() {
    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        plugin.logger.log(severityToLogLevel(severity), message)
        throwable?.printStackTrace()
    }

    fun log(severity: Severity, message: ComponentLike, tag: String, tagColor: TextColor? = null) {
        if (severity >= Severity.Warn)
            log(severity, message.asComponent().toPlainText(), "", null)
        else
            Bukkit.getConsoleSender().sendMessage(Component.text("[$tag] ").run {
                if (tagColor != null) color(tagColor) else this
            }.append(message))
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

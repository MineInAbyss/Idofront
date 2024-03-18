package com.mineinabyss.idofront.messaging

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.StaticConfig
import com.mineinabyss.idofront.messaging.IdoLogging.errorComp
import com.mineinabyss.idofront.messaging.IdoLogging.successComp
import com.mineinabyss.idofront.textcomponents.miniMsg
import com.mineinabyss.idofront.textcomponents.toPlainText
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.format.TextColor
import org.bukkit.plugin.Plugin

open class ComponentLogger(
    staticConfig: StaticConfig,
    tag: String,
) : Logger(staticConfig, tag) {
    fun i(message: ComponentLike) {
        if (config.minSeverity <= Severity.Info)
            logComponent(Severity.Info, message)
    }

    fun iMM(message: String) {
        i(message.miniMsg())
    }

    @Deprecated("Replaced with s", ReplaceWith("s(message)"))
    fun iSuccess(message: String) = s(message)

    @Deprecated("Replaced with s", ReplaceWith("s(message)"))
    fun iSuccess(message: ComponentLike) = s(message)

    @Deprecated("Replaced with f", ReplaceWith("f(message)"))
    fun iFail(message: String) = f(message)

    @Deprecated("Replaced with f", ReplaceWith("f(message)"))
    fun iFail(message: ComponentLike) = f(message)

    /**
     * Sends a green success message with an emote prefixed.
     * Uses default console colors when [severity] is above [Severity.Warn]
     */
    fun s(message: String, severity: Severity = Severity.Info) {
        s(message.miniMsg(), severity)
    }

    /**
     * Sends a green success message with an emote prefixed.
     * Uses default console colors when [severity] is above [Severity.Warn]
     */
    fun s(message: ComponentLike, severity: Severity = Severity.Info) {
        if (config.minSeverity <= severity)
            logComponent(Severity.Info, successComp.append(message), TextColor.color(0x008000))
    }

    /**
     * Sends a red failure message with an emote prefixed.
     * Should only be used to notify users of a handled error, ex. config failing to load and thus being skipped.
     * Uses default console colors when [severity] is above [Severity.Warn]
     */
    fun f(message: String, severity: Severity = Severity.Info) {
        f(message.miniMsg(), severity)
    }

    /**
     * Sends a red failure message with an emote prefixed.
     * Should only be used to notify users of a handled error, ex. config failing to load and thus being skipped.
     * Uses default console colors when [severity] is above [Severity.Warn]
     */
    fun f(message: ComponentLike, severity: Severity = Severity.Info) {
        if (config.minSeverity <= severity)
            logComponent(Severity.Info, errorComp.append(message), TextColor.color(0xFF0000))
    }

    fun v(message: ComponentLike) {
        if (config.minSeverity <= Severity.Verbose)
            logComponent(Severity.Verbose, message)
    }

    fun d(message: ComponentLike) {
        if (config.minSeverity <= Severity.Debug)
            logComponent(Severity.Debug, message)
    }

    fun w(message: ComponentLike) {
        if (config.minSeverity <= Severity.Warn)
            logComponent(Severity.Warn, message)
    }

    fun e(message: ComponentLike) {
        if (config.minSeverity <= Severity.Error)
            logComponent(Severity.Error, message)
    }

    fun a(message: ComponentLike) {
        if (config.minSeverity <= Severity.Assert)
            logComponent(Severity.Assert, message)
    }

    fun logComponent(severity: Severity, message: ComponentLike, tagColor: TextColor? = null) {
        config.logWriterList.forEach {
            if (!it.isLoggable(tag, severity)) return@forEach
            if (it is KermitPaperWriter) it.log(severity, message, tag, tagColor)
            else it.log(severity, message.asComponent().toPlainText(), tag, null)
        }
    }

    companion object {
        fun forPlugin(plugin: Plugin, minSeverity: Severity = Severity.Info): ComponentLogger {
            return ComponentLogger(
                StaticConfig(minSeverity = minSeverity, logWriterList = listOf(KermitPaperWriter(plugin))),
                plugin.name
            )
        }

        fun fallback(
            minSeverity: Severity = Severity.Info,
            tag: String = "Idofront"
        ): ComponentLogger {
            return ComponentLogger(StaticConfig(minSeverity = minSeverity), tag)
        }
    }
}

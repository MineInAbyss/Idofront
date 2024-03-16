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

    fun iSuccess(message: String) {
        iSuccess(message.miniMsg())
    }

    fun iSuccess(message: ComponentLike) {
        if (config.minSeverity <= Severity.Info)
            logComponent(Severity.Info, successComp.append(message), TextColor.color(0x008000))
    }

    fun iFail(message: String) {
        iFail(message.miniMsg())
    }

    fun iFail(message: ComponentLike) {
        if (config.minSeverity <= Severity.Info)
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
            if (!it.isLoggable(severity)) return@forEach
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

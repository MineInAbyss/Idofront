package com.mineinabyss.idofront.messaging

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.StaticConfig
import com.mineinabyss.idofront.messaging.IdoLogging.errorComp
import com.mineinabyss.idofront.messaging.IdoLogging.successComp
import com.mineinabyss.idofront.textcomponents.miniMsg
import com.mineinabyss.idofront.textcomponents.toPlainText
import net.kyori.adventure.text.ComponentLike
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
        i(successComp.append(message.miniMsg()))
    }

    fun iSuccess(message: ComponentLike) {
        i(successComp.append(message))
    }

    fun iFail(message: String) {
        i(errorComp.append(message.miniMsg()))
    }

    fun iFail(message: ComponentLike) {
        i(errorComp.append(message))
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

    fun logComponent(severity: Severity, message: ComponentLike) {
        config.logWriterList.forEach {
            if (!it.isLoggable(severity)) return@forEach
            if (it is KermitPaperWriter) it.log(severity, message)
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

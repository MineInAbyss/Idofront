package com.mineinabyss.idofront.features

import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import com.mineinabyss.idofront.commands.brigadier.IdoRootCommand
import com.mineinabyss.idofront.messaging.ComponentLogger
import com.mineinabyss.idofront.plugin.unregisterListeners
import kotlinx.coroutines.Job
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.koin.core.Koin
import org.koin.core.scope.Scope

@DslMarker
annotation class FeatureDSLMarker

@FeatureDSLMarker
interface FeatureDSL

fun feature(name: String, block: FeatureBuilder.() -> Unit): Feature<Unit> {
    return FeatureBuilder(name, Unit::class).apply(block).build()
}

@JvmName("featureWithType")
inline fun <reified T : Any> feature(name: String, block: FeatureBuilder.() -> Unit): Feature<T> {
    return FeatureBuilder(name, T::class).apply(block).build()
}

data class MainCommand(
    val names: List<String>,
    val description: String?,
    val reloadCommandName: String? = null,
    val reloadCommandPermission: String? = null,
    val permission: String? = null,
) {
    internal val subcommands = mutableListOf<context(Koin) IdoRootCommand.() -> Unit>()
    fun subcommand(block: context(Koin) IdoRootCommand.() -> Unit) {
        subcommands += block
    }
}

class FeatureCreate(val scope: Scope) : FeatureDSL {
    val plugin = scope.get<Plugin>()
    val logger get() = scope.get<ComponentLogger>()

    private val listeners = mutableListOf<Listener>()
    private val autoCloseables = mutableListOf<AutoCloseable>()
    private val tasks = mutableListOf<Job>()

    fun listeners(vararg listeners: Listener) {
        this.listeners += listeners
        for (listener in listeners) {
            try {
                plugin.server.pluginManager.registerSuspendingEvents(listener, plugin)
            } catch (_: IllegalArgumentException) {
                // Fallback in mocked tests where MCCoroutine can't correctly inject
                plugin.server.pluginManager.registerEvents(listener, plugin)
            }
        }
    }

    fun addCloseables(vararg autoClose: AutoCloseable) {
        autoCloseables += autoClose
    }

    fun addCloseables(autoClose: Collection<AutoCloseable>) {
        autoCloseables += autoClose
    }

    fun task(job: Job) {
        tasks += job
    }

    inline fun <reified T : Any> get(): T {
        return scope.get<T>()
    }

    fun close() {
        autoCloseables.reversed().forEach { it.close() }
        plugin.unregisterListeners(*listeners.toTypedArray())
        tasks.forEach { it.cancel() }
        scope.close()
    }
}

package com.mineinabyss.idofront.features

import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import com.mineinabyss.idofront.commands.brigadier.IdoRootCommand
import com.mineinabyss.idofront.commands.brigadier.context.IdoCommandContext
import com.mineinabyss.idofront.plugin.unregisterListeners
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.kodein.di.DirectDI
import org.kodein.di.direct
import org.kodein.di.instance

@DslMarker
annotation class FeatureDSLMarker

@FeatureDSLMarker
interface FeatureDSL

interface FeatureDI : DirectDI

fun FeatureDI.addCloseable(block: AutoCloseable) {
    instance<FeatureContext>().onClose.add(block)
}

fun FeatureDI.addCloseables(vararg closeable: AutoCloseable) {
    closeable.forEach { instance<FeatureContext>().onClose.add(it) }

}

context(di: DirectDI)
inline fun <reified T : Any> get() = di.instance<T>()


context(di: DirectDI)
inline val plugin get() = di.instance<Plugin>()

fun FeatureDI.listeners(vararg listeners: Listener) {
    val plugin = instance<Plugin>()
    val manager = plugin.server.pluginManager

    addCloseable { plugin.unregisterListeners(*listeners) }
    for (listener in listeners) {
        try {
            manager.registerSuspendingEvents(listener, plugin)
        } catch (_: IllegalArgumentException) {
            // Fallback in mocked tests where MCCoroutine can't correctly inject
            manager.registerEvents(listener, plugin)
        }
    }
}

fun FeatureDI.task(job: Job) {
    val scope = instance<CoroutineScope>()
    scope.launch { job.join() }
}

fun feature(name: String, block: FeatureBuilder.() -> Unit): Feature<Unit> {
    return FeatureBuilder(name, Unit::class).apply(block).build(extract = { })
}

@JvmName("featureWithType")
inline fun <reified T : Any> feature(name: String, block: FeatureBuilder.() -> Unit): Feature<T> {
    return FeatureBuilder(name, T::class).apply(block).build(extract = { instance<T>() })
}

data class DICommandContext(val manager: FeatureManager, val feature: Feature<*>)

context(di: DICommandContext, _: IdoCommandContext)
inline fun <reified T : Any> get(): T = di.manager.getInstance(di.feature)?.di?.direct?.instance<T>() ?: error("Command tried to get feature config of an unloaded feature: ${di.feature.name}.")

data class MainCommand(
    val names: List<String>,
    val description: String?,
    val reloadCommandName: String? = null,
    val reloadCommandPermission: String? = null,
    val permission: String? = null,
) {
    internal val subcommands = mutableListOf<context(DICommandContext) IdoRootCommand.() -> Unit>()
    fun subcommand(block: context(DICommandContext) IdoRootCommand.() -> Unit) {
        subcommands += block
    }
}

//class FeatureCreate(val scope: Scope) : FeatureDSL {
//    val plugin = scope.get<Plugin>()
//    val logger get() = scope.get<ComponentLogger>()
//
//    private val listeners = mutableListOf<Listener>()
//    private val autoCloseables = mutableListOf<AutoCloseable>()
//    private val tasks = mutableListOf<Job>()
//
//
//    inline fun <reified T : Any> get(): T {
//        return scope.get<T>()
//    }
//
//    fun close() {
//        autoCloseables.reversed().forEach { it.close() }
//        plugin.unregisterListeners(*listeners.toTypedArray())
//        tasks.forEach { it.cancel() }
//        scope.close()
//    }
//}

package com.mineinabyss.idofront.features

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import com.mineinabyss.dependencies.*
import com.mineinabyss.idofront.config.ConfigBuilder
import com.mineinabyss.idofront.config.SingleConfig
import com.mineinabyss.idofront.config.config
import com.mineinabyss.idofront.messaging.ComponentLogger
import org.bukkit.plugin.Plugin
import kotlin.io.path.div

/**
 * Injects a `SingleConfig<T>` based on serializable type [T], located at [path] relative to the plugin's data folder.
 *
 * Also injects [T] directly as a binding for [SingleConfig.getCachedOrRead].
 * [SingleConfig.updateCached] can be used to refresh the config on later reads.
 *
 * For more complicated config use-cases (ex. reading a directory), use [ConfigBuilder] and manually inject via a context class.
 *
 * ### Example usage:
 *
 * ```kotlin
 * // Inject, overriding format to Yaml
 * singleConfig<MyConfig>("config.yml") { format = Yaml() }
 *
 * // Gets updated config if requesting on a new server tick
 * val config = get<MyConfig>()
 *
 * // Write newConfig to disk
 * get<SingleConfig<MyConfig>>().write(newConfig)
 * get<MyConfig>() == newConfig // true, since write refreshed the cached config.
 * ```
 */
inline fun <reified T : Any> MutableDI.singleConfig(
    path: String,
    crossinline configure: ConfigBuilder<T>.() -> Unit = {},
): InjectedValue<T> {
    val configHolder by single<SingleConfig<T>> {
        val plugin = get<Plugin>()
        config<T> { configure() }.single(plugin.dataPath / path)
    }
    return factory<T> { configHolder.getCachedOrRead() }
}

/**
 * Injects a [Logger] and [ComponentLogger] for the given [plugin].
 *
 * In the future might be updated to allow re-reading severity from a config after reloads, for now [minSeverity] is evaluated once.
 *
 * @param minSeverity Provider for the minimum severity to log, can read DI values.
 */
fun MutableDI.singlePluginLogger(
    plugin: Plugin,
    minSeverity: DI.() -> Severity = { Severity.Info },
) = single { ComponentLogger.forPlugin(plugin, minSeverity()) }.and<Logger>()
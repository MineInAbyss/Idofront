package com.mineinabyss.idofront.features

import co.touchlab.kermit.Logger
import com.mineinabyss.dependencies.MutableDI
import com.mineinabyss.dependencies.and
import com.mineinabyss.dependencies.get
import com.mineinabyss.dependencies.single
import com.mineinabyss.idofront.config.ConfigBuilder
import com.mineinabyss.idofront.config.config
import com.mineinabyss.idofront.messaging.ComponentLogger
import org.bukkit.plugin.Plugin
import kotlin.io.path.div

/**
 * Injects a single serializable config of type [T], located at [path] relative to the plugin's data folder.
 *
 * For more complicated config use-cases (ex. reading a directory), use [com.mineinabyss.idofront.config.ConfigBuilder] and manually inject via a context class.
 */

inline fun <reified T : Any> MutableDI.singleConfig(
    path: String,
    crossinline configure: ConfigBuilder<T>.() -> Unit = {},
) = single<T> {
    val plugin = get<Plugin>()
    config<T> { configure() }.single(plugin.dataPath / path).read()
}

fun MutableDI.singlePluginLogger(plugin: Plugin) = single { ComponentLogger.forPlugin(plugin) }.and<Logger>()
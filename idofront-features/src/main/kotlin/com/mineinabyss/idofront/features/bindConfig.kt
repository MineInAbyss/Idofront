package com.mineinabyss.idofront.features

import com.mineinabyss.idofront.config.ConfigBuilder
import com.mineinabyss.idofront.config.config
import org.bukkit.plugin.Plugin
import org.kodein.di.DI
import org.kodein.di.DirectDI
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import kotlin.io.path.div

/**
 * Injects a single serializable config of type [T], located at [path] relative to the plugin's data folder.
 *
 * For more complicated config use-cases (ex. reading a directory), use [com.mineinabyss.idofront.config.ConfigBuilder] and manually inject via a context class.
 */

inline fun <reified T : Any> DI.Builder.bindConfig(
    path: String,
    crossinline configure: context(DirectDI) ConfigBuilder<T>.() -> Unit = {},
) {
    bindSingleton {
        val plugin = instance<Plugin>()
        config<T> { configure() }.single(plugin.dataPath / path).read()
    }
}
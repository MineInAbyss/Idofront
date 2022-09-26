package com.mineinabyss.idofront.config

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import kotlinx.serialization.serializer
import org.bukkit.plugin.Plugin
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.error.KoinAppAlreadyStartedException
import org.koin.core.module.Module
import java.nio.file.Path
import kotlin.io.path.div

fun startOrAppendKoin(vararg modules: Module) {
    try {
        startKoin { modules(*modules) }
    } catch (e: KoinAppAlreadyStartedException) {
        loadKoinModules(modules.toList())
    }
}

inline fun <reified T> Module.singleConfig(
    plugin: Plugin,
    serializer: KSerializer<T> = serializer(),
    path: Path = plugin.dataFolder.toPath() / "config.yml",
    format: StringFormat = Yaml(configuration = YamlConfiguration(strictMode = false)),
    crossinline unload: ReloadScope.(conf: T) -> Unit = {},
    crossinline load: ReloadScope.(conf: T) -> Unit = {}
) {
    val config = object: IdofrontConfig<T>(plugin, serializer, path, format) {
        override fun ReloadScope.load() {
            load(data)
        }

        override fun ReloadScope.unload() {
            unload(data)
        }
    }
    factory { config.data }
}

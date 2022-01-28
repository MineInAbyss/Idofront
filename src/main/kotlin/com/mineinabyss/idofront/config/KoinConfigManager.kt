package com.mineinabyss.idofront.config

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
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

//class LoadedConfig(
//    val module: Module,
//    val idofrontConfig: IdofrontConfig<*>
//)

//val loadedConfigs = mutableMapOf<KClassifier, LoadedConfig>()

inline fun <reified T> Module.singleConfig(
    serializer: KSerializer<T>,
    plugin: Plugin,
    path: Path = plugin.dataFolder.toPath() / "config.yml",
    format: StringFormat = Yaml(configuration = YamlConfiguration(strictMode = false))
) {
    val config = IdofrontConfig(plugin, serializer, path, format)
    factory<T> { config.data }
//    loadedConfigs[T::class] = LoadedConfig(this, config)
}

package com.mineinabyss.idofront.config

import com.mineinabyss.idofront.messaging.logSuccess
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.serializer
import org.bukkit.plugin.Plugin
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.InputStream
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.div
import kotlin.io.path.inputStream
import kotlin.io.path.isRegularFile

class ConfigBuilder<T>(
    val name: String,
    val serializer: KSerializer<T>
) {
    private var module = EmptySerializersModule()
    private var path = Path("")
    private var getInput: ((ext: String) -> InputStream?)? = null

    fun Plugin.saveDefault(ext: String, override: Boolean = false) {
        val fullPath = "${path / ext}.$ext"
        saveResource(fullPath, override)
        logSuccess("Loaded default config at $fullPath")
    }

    fun serialization(run: SerializersModuleBuilder.() -> Unit) {
        module = SerializersModule { run() }
    }

    fun fromPath(path: Path) {
        getInput = { ext -> (path / "$name.$ext").takeIf { it.isRegularFile() }?.inputStream() }
    }

    fun Plugin.fromPluginPath(relativePath: Path = Path("")) {
        fromPath(dataFolder.toPath() / relativePath)
    }

    fun fromInputStream(getInputStream: (ext: String) -> InputStream?) {
        getInput = getInputStream
    }

    fun build(): IdofrontConfig<T> = IdofrontConfig(
        name, serializer, module, getInput ?: error("Error building config $name, no input source provided")
    )
}

inline fun <reified T> config(
    name: String,
    serializer: KSerializer<T> = serializer(),
    run: ConfigBuilder<T>.() -> Unit = {}
): IdofrontConfig<T> {
    return ConfigBuilder(name, serializer).apply(run).build()
}

fun Plugin.main() {
    startKoin {
        module {
            singleConfig(config<String>("test") {
                fromPluginPath()
                saveDefault("yml")
            })
        }
    }
}

/**
 * Adds this config's data type to the module, data will be updated if the config is reloaded and using `inject`.
 */
inline fun <reified T> Module.singleConfig(config: IdofrontConfig<T>) {
    // config never changes, but we register a factory to access the latest data object, in case it is updated
    factory { config.data }
}

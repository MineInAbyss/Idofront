package com.mineinabyss.idofront.config

import com.mineinabyss.idofront.messaging.logSuccess
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.serializer
import org.bukkit.plugin.Plugin
import java.io.InputStream
import java.nio.file.Path
import kotlin.io.path.*

class IdofrontConfigBuilder<T>(
    val name: String,
    val serializer: KSerializer<T>
) {
    private var module = EmptySerializersModule()
    private var getInput: ((ext: String) -> InputStream?)? = null

    fun serialization(run: SerializersModuleBuilder.() -> Unit) {
        module = SerializersModule { run() }
    }

    fun fromPath(path: Path) {
        getInput = { ext -> (path / "$name.$ext").takeIf { it.isRegularFile() }?.inputStream() }
    }

    fun Plugin.fromPluginPath(relativePath: Path = Path(""), loadDefault: Boolean = false) {
        fromPath(dataFolder.toPath() / relativePath)
        if (loadDefault) {
            val (inputStream, path) = IdofrontConfig.supportedFormats.firstNotNullOfOrNull {
                val path = "$relativePath/$name.$it"
                getResource(path)?.to(path)
            } ?: error("Could not find config in plugin resources at $relativePath/$name.<format>")
            val outFile = dataFolder.toPath() / path
            outFile.createDirectories()
            outFile.createFile()
            outFile.outputStream().use {
                inputStream.copyTo(it)
                inputStream.close()
            }
            logSuccess("Loaded default config at $path/$name")
        }
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
    run: IdofrontConfigBuilder<T>.() -> Unit = {}
): IdofrontConfig<T> {
    return IdofrontConfigBuilder(name, serializer).apply(run).build()
}

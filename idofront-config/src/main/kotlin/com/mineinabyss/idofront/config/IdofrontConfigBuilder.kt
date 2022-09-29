package com.mineinabyss.idofront.config

import com.mineinabyss.idofront.messaging.logSuccess
import com.mineinabyss.idofront.messaging.logWarn
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
    val fileName: String,
    val serializer: KSerializer<T>
) {
    private var module = EmptySerializersModule()
    private var getInput: ((ext: String) -> InputStream?)? = null

    fun serialization(run: SerializersModuleBuilder.() -> Unit) {
        module = SerializersModule { run() }
    }

    fun fromPath(path: Path) {
        getInput = { ext -> (path / "$fileName.$ext").takeIf { it.isRegularFile() }?.inputStream() }
    }

    fun Plugin.fromPluginPath(relativePath: Path = Path(""), loadDefault: Boolean = false) {
        val fileWithoutExt = relativePath / fileName
        val dataPath = dataFolder.toPath()
        fromPath(dataPath / relativePath)

        if (loadDefault || !dataPath.toFile().exists() || dataPath.listDirectoryEntries("$fileWithoutExt.*").isEmpty()) {
            val (inputStream, path) = IdofrontConfig.supportedFormats.firstNotNullOfOrNull { ext ->
                val path = "$fileWithoutExt.$ext"
                getResource(path)?.to(path)
            } ?: error("Could not find config in plugin resources at $relativePath/$fileName.<format>")
            val outFile = dataPath / path

            // Prevent warning msg if file already exists
            if (!outFile.toFile().exists() || outFile.readLines().isEmpty()) {
                saveResource(path, false)
                logWarn("Could not find config at $outFile, creating it from default.")
                logSuccess("Loaded default config at $outFile")
            } else {// Ideally we would read the file and add any missing entries
                logSuccess("Loaded config at $outFile")
            }
        }
    }

    fun fromInputStream(getInputStream: (ext: String) -> InputStream?) {
        getInput = getInputStream
    }

    fun build(): IdofrontConfig<T> = IdofrontConfig(
        fileName, serializer, module, getInput ?: error("Error building config $fileName, no input source provided")
    )
}

inline fun <reified T> config(
    fileName: String,
    serializer: KSerializer<T> = serializer(),
    run: IdofrontConfigBuilder<T>.() -> Unit = {}
): IdofrontConfig<T> {
    return IdofrontConfigBuilder(fileName, serializer).apply(run).build()
}

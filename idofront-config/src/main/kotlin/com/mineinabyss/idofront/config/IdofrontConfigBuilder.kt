package com.mineinabyss.idofront.config

import com.mineinabyss.idofront.messaging.logSuccess
import com.mineinabyss.idofront.messaging.logWarn
import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.serializer
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.file.Path
import kotlin.io.path.*

class IdofrontConfigBuilder<T>(
    val fileName: String,
    val serializer: KSerializer<T>
) {
    private var serializersModule = EmptySerializersModule()
    private var getInput: ((ext: String) -> InputStream?)? = null
    private var formats: (SerializersModule) -> Map<String, StringFormat> = { mapOf() }

    fun formats(map: (sharedModule: SerializersModule) -> Map<String, StringFormat>) {
        formats = map
    }

    fun serializersModule(run: SerializersModuleBuilder.() -> Unit) {
        serializersModule = SerializersModule { run() }
    }

    fun fromPath(path: Path) {
        getInput = { ext -> (path / "$fileName.$ext").takeIf { it.isRegularFile() }?.inputStream() }
    }

    fun Plugin.fromPluginPath(relativePath: Path = Path(""), loadDefault: Boolean = false) {
        val fileWithoutExt = relativePath / fileName
        val dataPath = dataFolder.toPath()
        fromPath(dataPath / relativePath)

        if (loadDefault || !dataPath.toFile().exists() || dataPath.listDirectoryEntries("$fileWithoutExt.*")
                .isEmpty()
        ) {
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
            } else {
                if (outFile.toFile().extension == "yml")
                    validateYamlConfig(outFile).save(outFile.toFile())
                logSuccess("Loaded config at $outFile")
            }
        }
    }

    fun fromInputStream(getInputStream: (ext: String) -> InputStream?) {
        getInput = getInputStream
    }

    fun build(): IdofrontConfig<T> = IdofrontConfig(
        fileName,
        serializer,
        serializersModule,
        formats(serializersModule),
        getInput ?: error("Error building config $fileName, no input source provided")
    )

    /** Validates config file by removing entries not found in default, and adding missing ones */
    private fun Plugin.validateYamlConfig(outFile: Path): YamlConfiguration {
        val currentConfig = YamlConfiguration.loadConfiguration(outFile.toFile())
        val defaultConfig = extractDefault(outFile.toFile().name) ?: return currentConfig

        // Removes old or invalid entries
        for (key in currentConfig.getKeys(true)) {
            if (defaultConfig.get(key) == null) {
                logWarn("Could not find entry for $key in default config, removing it.")
                currentConfig.set(key, defaultConfig.get(key))
            }
        }

        // Add missing config entries
        for (key in defaultConfig.getKeys(true)) {
            if (currentConfig.get(key) == null) {
                logWarn("Could not find config entry $key in $outFile, adding it from default.")
                currentConfig.set(key, defaultConfig.get(key))
            }
        }

        return currentConfig
    }

    private fun Plugin.extractDefault(source: String): YamlConfiguration? {
        val inputStreamReader = InputStreamReader(getResource(source) ?: return null)
        return try {
            YamlConfiguration.loadConfiguration(inputStreamReader)
        } finally {
            try {
                inputStreamReader.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}

inline fun <reified T> config(
    fileName: String,
    serializer: KSerializer<T> = serializer(),
    run: IdofrontConfigBuilder<T>.() -> Unit = {}
): IdofrontConfig<T> {
    return IdofrontConfigBuilder(fileName, serializer).apply(run).build()
}

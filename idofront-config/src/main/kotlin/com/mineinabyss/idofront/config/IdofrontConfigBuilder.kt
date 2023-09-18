package com.mineinabyss.idofront.config

import com.mineinabyss.idofront.messaging.logSuccess
import com.mineinabyss.idofront.messaging.logWarn
import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleBuilder
import org.bukkit.plugin.Plugin
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Path
import kotlin.io.path.*

class IdofrontConfigBuilder<T>(
    val fileName: String,
    val serializer: KSerializer<T>
) : IdofrontConfigDSL<T> {
    private var serializersModule = EmptySerializersModule()
    private var getInput: ((ext: String) -> InputStream?)? = null
    private var getOutput: ((ext: String) -> OutputStream?)? = null
    private var formats: (SerializersModule) -> Map<String, StringFormat> = { mapOf() }

    override var mergeUpdates: Boolean = false

    override fun formats(map: (sharedModule: SerializersModule) -> Map<String, StringFormat>) {
        formats = map
    }

    override fun serializersModule(run: SerializersModuleBuilder.() -> Unit) {
        serializersModule = SerializersModule { run() }
    }

    override fun fromPath(path: Path) {
        fun file(ext: String) = (path / "$fileName.$ext").takeIf { it.isRegularFile() }
        getInput = { ext -> file(ext)?.inputStream() }
        getOutput = { ext -> file(ext)?.outputStream() }
    }
    // Extract reused code into a function:

    override fun Plugin.fromPluginPath(relativePath: Path, loadDefault: Boolean) {
        val fileWithoutExt = relativePath / fileName
        val dataPath = dataFolder.toPath()
        fromPath(dataPath / relativePath)

        if (loadDefault
            || !dataPath.toFile().exists()
            || dataPath.listDirectoryEntries("$fileWithoutExt.*").isEmpty()
        ) {
            // Look in jar for a config of supported formats
            val path = IdofrontConfig.supportedFormats.firstNotNullOfOrNull { ext ->
                val path = "$fileWithoutExt.$ext".replace("\\", "/")
                path.takeIf { getResource(path) != null }
            } ?: error("Could not find config in plugin resources at $relativePath/$fileName.<format>")

            // Write default config if it doesn't exist
            val outFile = dataPath / path
            if (!outFile.toFile().exists() || outFile.readLines().isEmpty()) {
                saveResource(path, false)
                logWarn("Could not find config at $outFile, creating it from default.")
                logSuccess("Loaded default config at $outFile")
            }
        }
    }

    override fun fromInputStream(getInputStream: (ext: String) -> InputStream?) {
        getInput = getInputStream
    }

    override fun toOutputStream(getOutputStream: (ext: String) -> OutputStream?) {
        getOutput = getOutputStream
    }

    override fun build(): IdofrontConfig<T> = IdofrontConfig(
        fileName,
        serializer,
        serializersModule,
        formats(serializersModule),
        getInput ?: error("Error building config $fileName, no input source provided"),
        getOutput,
        mergeUpdates,
    )
}

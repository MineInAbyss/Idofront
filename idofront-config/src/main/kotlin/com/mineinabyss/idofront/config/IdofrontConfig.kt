package com.mineinabyss.idofront.config

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.modules.SerializersModule
import org.bukkit.plugin.Plugin
import java.io.InputStream
import kotlin.io.path.*

/**
 * Stores configuration data for your config files
 *
 * @param T The type of the serializable class that holds data for this config
 * @param plugin The plugin this config belongs to.
 * @param serializer [T]'s serializer
 * @param fileWithoutExt Defaults to your `config.yml` inside [plugin]'s [data folder][Plugin.getDataFolder].
 * @param format The serialization format. Defaults to YAML.
 */
class IdofrontConfig<T>(
    val name: String,
    val serializer: KSerializer<T>,
    val module: SerializersModule,
    val getInput: (ext: String) -> InputStream?
) {
    val formats = mapOf(
        "yml" to Yaml(serializersModule = module, YamlConfiguration(strictMode = false)),
        "json" to Json {
            serializersModule = module
        }
    )

    /** The deserialized data for this configuration. */
    var data: T = loadData()
        private set

    /** Discards current data and re-reads and serializes it */
    @OptIn(ExperimentalSerializationApi::class)
    private fun loadData(): T {
        formats.forEach { (ext, format) ->
            val input = getInput(ext) ?: return@forEach
            input.use {
                return when (format) {
                    is Yaml -> format.decodeFromStream(serializer, input)
                    is Json -> format.decodeFromStream(serializer, input)
                    else -> format.decodeFromString(serializer, input.bufferedReader().lineSequence().joinToString())
                }.also { data = it }
            }
        }
        error("Could not load a config file: $name of type ${serializer.descriptor.serialName}")
    }

    fun reload() {
        loadData()
    }

    companion object {
        val supportedFormats = listOf("yml", "json")
    }
}

package com.mineinabyss.idofront.config

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.mineinabyss.idofront.messaging.logSuccess
import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import kotlinx.serialization.modules.SerializersModule
import org.bukkit.plugin.Plugin
import java.io.InputStream
import java.io.OutputStream
import kotlin.io.path.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

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
    val fileName: String,
    val serializer: KSerializer<T>,
    val serializersModule: SerializersModule,
    formatOverrides: Map<String, StringFormat>,
    val getInput: (ext: String) -> InputStream?,
    val getOutput: ((ext: String) -> OutputStream?)?,
    val mergeUpdates: Boolean,
): ReadOnlyProperty<Any?, T> {
    val formats = mapOf(
        "yml" to Yaml(
            serializersModule = serializersModule,
            YamlConfiguration(encodeDefaults = true, strictMode = false)
        ),
        "json" to Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
            prettyPrint = true
            serializersModule = this@IdofrontConfig.serializersModule
        }
    ) + formatOverrides

    /** The deserialized data for this configuration. */
    var data: T = loadData()
        private set

    private fun decode(format: StringFormat, input: InputStream) = when (format) {
        is Yaml -> format.decodeFromStream(serializer, input)
        is Json -> format.decodeFromStream(serializer, input)
        else -> format.decodeFromString(serializer, input.bufferedReader().lineSequence().joinToString())
    }

    private fun encode(format: StringFormat, output: OutputStream, data: T) {
        when (format) {
            is Yaml -> format.encodeToStream(serializer, data, output)
            is Json -> format.encodeToStream(serializer, data, output)
            else -> format.encodeToString(serializer, data).byteInputStream().copyTo(output)
        }
    }

    /** Discards current data and re-reads and serializes it */
    private fun loadData(): T {
        formats.forEach { (ext, format) ->
            val input = getInput(ext) ?: return@forEach
            return input.use {
                decode(format, input).also {
                    if (mergeUpdates) {
                        getOutput?.invoke(ext)?.use { output ->
                            encode(format, output, it)
                        }
                    }
                    data = it
                    logSuccess("Loaded config: $fileName.$ext")
                }
            }
        }
        error("Could not load a config file: $this")
    }

    fun reload() {
        loadData()
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = data

    override fun toString(): String = "$fileName of type ${serializer.descriptor.serialName}"

    companion object {
        val supportedFormats = listOf("yml", "json")
    }
}

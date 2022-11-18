package com.mineinabyss.idofront.config

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.mineinabyss.idofront.messaging.logSuccess
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.modules.SerializersModule
import org.bukkit.plugin.Plugin
import java.io.InputStream
import java.nio.file.Path
import kotlin.io.path.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

internal typealias Formats = Map<String, StringFormat>

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
    val formats: Formats,
    val inputGetter: InputGetter,
    val saveDirectory: Path?,
): ReadOnlyProperty<Any?, T> {

    /** The deserialized data for this configuration. */
    var data: T = loadData()
        private set

    fun loadData() {

    }

    fun loadDefault(): T? {
        if(default == null) return null
        if(saveDirectory == null) error("Cannot load default config without a save directory")
        // If no file exists with any extension, write default

        return null
    }

    fun reload() {
        loadData()
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = data

    override fun toString(): String = "$fileName of type ${serializer.descriptor.serialName}"
}

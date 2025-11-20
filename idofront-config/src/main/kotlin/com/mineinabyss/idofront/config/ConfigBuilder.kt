package com.mineinabyss.idofront.config

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus

/**
 * Builder for a [Config] definition.
 *
 * @see config
 */
data class ConfigBuilder<T>(
    /** Serializer to use for decoding the config file. */
    var serializer: KSerializer<T>,
    /** Format to use for decoding the config file. */
    var format: StringFormat = Yaml(
        configuration = YamlConfiguration(
            strictMode = false
        )
    ),
) {
    /** Defines when the decoded config file should be written back to (ex. to add new fields, or encode a default config.) */
    var writeBack: WriteMode = WriteMode.WHEN_EMPTY

    /** Provide a default config to use if decoding fails. Only applicable when using as [SingleConfig]. */
    var default: T? = null

    /**
     * Helper to add a [SerializersModule] to the [StringFormat] being used to decode the config.
     *
     * Note: only supports [Yaml] and [Json] formats, for others, manually set the [format].
     */
    fun withSerializersModule(module: SerializersModule) {
        format = when (val format = format) {
            is Yaml -> Yaml(format.serializersModule + module, format.configuration)
            is Json -> Json(format) { serializersModule += module }
            else -> error("Could not automatically add serializers module to unknown format $format, manually add it instead.")
        }
    }

    fun build() = Config(
        serializer = serializer,
        format = format,
        writeBack = writeBack,
        default = default,
    )
}
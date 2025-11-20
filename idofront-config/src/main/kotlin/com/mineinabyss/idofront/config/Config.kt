package com.mineinabyss.idofront.config

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import java.nio.file.Path
import kotlin.io.path.createFile
import kotlin.io.path.createParentDirectories
import kotlin.io.path.inputStream
import kotlin.io.path.notExists
import kotlin.io.path.outputStream

/**
 * Config decoding definition, built via [ConfigBuilder].
 *
 * Can be used to read/write a config from a single path, or configs from a directory.
 */
data class Config<T>(
    val serializer: KSerializer<T>,
    val format: StringFormat,
    val default: T?,
    val writeBack: WriteMode,
) {
    /**
     * Read and write a single config file located at [path]
     *
     * @see SingleConfig
     */
    fun single(path: Path) = SingleConfig(this, path)

    /**
     * Read and write many config files located in a directory at [path].
     *
     * @see DirectoryConfig
     */
    fun fromDirectory(
        path: Path,
        extension: String = inferExtensionFromFormat(),
    ): DirectoryConfig<T> = DirectoryConfig(this, path, extension)

    /**
     * Read and write many config files in a directory at [path], where each file contains
     * key-value pairs of strings to this config type [T].
     *
     * Each entry may also reuse parts of others using an `include` key that will be parsed
     * automatically, without the underlying serializer needing to know about it.
     *
     * @throws IllegalArgumentException if the [format] is not [Yaml].
     * @see MultiEntryYamlReader
     */
    fun multiEntry(path: Path): MultiEntryYamlReader<T> {
        return MultiEntryYamlReader(this, path, format as? Yaml ?: error("Format must be YAML"))
    }

    internal fun decode(path: Path): Result<T> = runCatching {
        SerializationHelpers.decode(format, serializer, path.inputStream())
    }

    internal fun encode(path: Path, data: T) = runCatching {
        if (path.notExists()) path.createParentDirectories().createFile()
        SerializationHelpers.encode(format, serializer, path.outputStream(), data)
    }

    internal fun inferExtensionFromFormat() = when (format) {
        is Yaml -> "yml"
        is Json -> "json"
        else -> error("Could not infer extension from unknown format $format")
    }
}
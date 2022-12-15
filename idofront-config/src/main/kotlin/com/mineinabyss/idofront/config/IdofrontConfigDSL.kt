package com.mineinabyss.idofront.config

import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.serializer
import org.bukkit.plugin.Plugin
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Path
import kotlin.io.path.Path

@IdofrontConfigDSLMarker
interface IdofrontConfigDSL<T> {
    /** If true, will re-serialize and write to output. */
    var mergeUpdates: Boolean

    /**
     * Specifies additional serialization formats based on file extension.
     *
     * Remember to pass `sharedModule` into the format's constructor.
     */
    fun formats(map: (sharedModule: SerializersModule) -> Map<String, StringFormat>)

    /** Sets a shared [SerializersModule] for all formats. */
    fun serializersModule(run: SerializersModuleBuilder.() -> Unit)

    /** Sets input to a file [path]. */
    fun fromPath(path: Path)

    /**
     * Sets input to a [path][relativePath] relative to the plugin's data folder.
     *
     * @param loadDefault If true, will load the default config from the plugin's jar if no config is found in the data folder.
     */
    fun Plugin.fromPluginPath(relativePath: Path = Path(""), loadDefault: Boolean = false)

    /** Sets input to an input stream directly */
    fun fromInputStream(getInputStream: (ext: String) -> InputStream?)

    /** Sets output to an input stream directly */
    fun toOutputStream(getOutputStream: (ext: String) -> OutputStream?)

    fun build(): IdofrontConfig<T>
}


/**
 * Creates and initializes a config for a serializable class [T]
 *
 * @see [IdofrontConfigDSL]
 */
inline fun <reified T> config(
    fileName: String,
    serializer: KSerializer<T> = serializer(),
    run: IdofrontConfigDSL<T>.() -> Unit = {}
): IdofrontConfig<T> {
    return IdofrontConfigBuilder(fileName, serializer).apply(run).build()
}

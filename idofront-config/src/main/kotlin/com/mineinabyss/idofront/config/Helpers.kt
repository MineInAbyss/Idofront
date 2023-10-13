package com.mineinabyss.idofront.config

import kotlinx.serialization.serializer
import org.bukkit.plugin.Plugin
import java.nio.file.Path

typealias IdofrontConfig<T> = Config<T>
typealias IdofrontFormat = Format
typealias IdofrontConfigFormats = ConfigFormats

inline fun <reified T> config(
    name: String,
    path: Path,
    default: T,
    formats: ConfigFormats = ConfigFormats(),
    mergeUpdates: Boolean = true,
    preferredFormat: String = "yml",
    lazyLoad: Boolean = false,
    noinline onFirstLoad: (T) -> Unit = {},
    noinline onReload: (T) -> Unit = {},
): Config<T> {
    return Config(
        name = name,
        path = path,
        default = default,
        serializer = serializer<T>(),
        preferredFormat = formats.extToFormat[preferredFormat]
            ?: error("Preferred format (with ext $preferredFormat) not found for config: $name"),
        formats = formats,
        mergeUpdates = mergeUpdates,
        lazyLoad = lazyLoad,
        onFirstLoad = onFirstLoad,
        onReload = onReload,
    )
}

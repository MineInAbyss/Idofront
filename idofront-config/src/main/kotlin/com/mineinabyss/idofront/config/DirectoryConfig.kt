package com.mineinabyss.idofront.config

import com.mineinabyss.idofront.messaging.idofrontLogger
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile
import kotlin.io.path.walk

class DirectoryConfig<T>(
    val config: Config<T>,
    val directory: Path,
    val extension: String,
) {
    fun read(): List<ConfigEntry<T>> = directory.walk()
        .filter { it.isRegularFile() && it.extension == extension }
        .mapNotNull { path ->
            val decoded = config.decode(path)
                .onFailure {
                    idofrontLogger.e { "Failed to read config file at $path" }
                    it.printStackTrace()
                }
                .getOrNull()
            if (decoded != null) ConfigEntry<T>(path, decoded) else null
        }
        .toList()

    fun readSingle(path: Path): T {
        return SingleConfig(config, path).read()
    }

    fun writeSingle(data: T, path: Path) = SingleConfig(config, path).write(data)
}
package com.mineinabyss.idofront.config

import java.nio.file.Path
import kotlin.io.path.notExists

class SingleConfig<T>(
    val config: Config<T>,
    val path: Path,
) {
    fun read(): T {
        if (config.writeBack == WriteMode.WHEN_EMPTY && path.notExists() && config.default != null) {
            write(config.default)
            return config.default
        }

        val decoded = config.decode(path).getOrElse { config.default ?: throw it }

        if (config.writeBack == WriteMode.ALWAYS) write(decoded)

        return decoded
    }

    fun write(data: T) {
        config.encode(path, data)
    }
}
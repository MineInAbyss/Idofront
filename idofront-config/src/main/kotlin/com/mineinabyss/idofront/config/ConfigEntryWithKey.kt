package com.mineinabyss.idofront.config

import java.nio.file.Path

data class ConfigEntryWithKey<T>(
    val path: Path,
    val key: String,
    val entry: T,
)
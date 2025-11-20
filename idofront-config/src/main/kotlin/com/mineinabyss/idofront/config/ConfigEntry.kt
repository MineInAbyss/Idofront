package com.mineinabyss.idofront.config

import java.nio.file.Path

data class ConfigEntry<T>(val path: Path, val data: T)
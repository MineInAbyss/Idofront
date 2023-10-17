package com.mineinabyss.idofront.features

import com.mineinabyss.idofront.config.IdofrontConfig

interface Configurable<T> {
    val configManager: IdofrontConfig<T>
    val config: T get() = configManager.getOrLoad()
}

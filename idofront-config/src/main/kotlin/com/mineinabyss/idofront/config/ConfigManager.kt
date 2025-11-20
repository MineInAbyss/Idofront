package com.mineinabyss.idofront.config

import kotlinx.serialization.serializer

/**
 * Entrypoint for Idofront's config helpers.
 */
inline fun <reified T> config(block: ConfigBuilder<T>.() -> Unit = {}): Config<T> {
    return ConfigBuilder(serializer<T>()).apply(block).build()
}

package com.mineinabyss.idofront.config

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

internal object DefaultFormats {
    fun with(module: SerializersModule) = mapOf(
        "yml" to Yaml(serializersModule = module, YamlConfiguration(strictMode = false)),
        "json" to Json {
            serializersModule = module
            ignoreUnknownKeys = true
        }
    )
}

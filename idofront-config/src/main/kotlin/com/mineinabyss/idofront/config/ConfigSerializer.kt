package com.mineinabyss.idofront.config

import com.charleskorn.kaml.Yaml
import com.mineinabyss.idofront.messaging.logSuccess
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

class ConfigSerializer<T>(
    val formats: Formats,
    val serializer: KSerializer<T>,
    val inputGetter: InputGetter,
) : ConfigGetter<T> {
    @OptIn(ExperimentalSerializationApi::class)
    override fun get(): Result<T> {
        val (input, ext) = inputGetter.read().getOrThrow()
        return runCatching {
            input.use {
                when (
                    val format = formats[ext]
                        ?: error("Could not find a config file '$this' for any supported extension (${formats.keys})")
                ) {
                    is Yaml -> format.decodeFromStream(serializer, input)
                    is Json -> format.decodeFromStream(serializer, input)
                    else -> format.decodeFromString(serializer, input.bufferedReader().lineSequence().joinToString())
                }.also {
                    logSuccess("Loaded config: $this")
                }
            }
        }
    }
}

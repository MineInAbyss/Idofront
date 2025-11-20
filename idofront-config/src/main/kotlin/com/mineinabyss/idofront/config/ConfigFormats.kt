package com.mineinabyss.idofront.config

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import com.charleskorn.kaml.encodeToStream
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.InputStream
import java.io.OutputStream

internal object SerializationHelpers {
    fun <T> decode(format: StringFormat, serializer: DeserializationStrategy<T>, input: InputStream): T =
        when (format) {
            is Yaml -> format.decodeFromStream(serializer, input)
            is Json -> format.decodeFromStream(serializer, input)
            else -> format.decodeFromString(serializer, input.bufferedReader().lineSequence().joinToString())
        }

    fun <T> encode(format: StringFormat, serializer: SerializationStrategy<T>, output: OutputStream, data: T) {
        when (format) {
            is Yaml -> format.encodeToStream(serializer, data, output)
            is Json -> format.encodeToStream(serializer, data, output)
            else -> format.encodeToString(serializer, data).byteInputStream().copyTo(output)
        }
    }
}

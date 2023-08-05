package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.time.ticks
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Duration

object DurationSerializer : KSerializer<Duration> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Time", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Duration) =
        encoder.encodeString(value.inWholeMilliseconds.toString())

    override fun deserialize(decoder: Decoder): Duration {
        val string = decoder.decodeString()
        return Duration.parseOrNull(string) ?: fromString(decoder.decodeString()) ?: error("Not a valid duration: $string")
    }

    private fun fromString(string: String): Duration? {
        val splitAt = string.indexOfFirst { it.isLetter() }.takeIf { it > 0 } ?: string.length
        val value = string.take(splitAt).toDouble()
        return if (string.drop(splitAt) == "t") value.toInt().ticks
        else null
    }
}

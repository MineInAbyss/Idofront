package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.time.ticks
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object DurationSerializer : KSerializer<Duration> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Time", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Duration) =
        encoder.encodeString(value.inWholeMilliseconds.toString())

    override fun deserialize(decoder: Decoder): Duration {
        val string = decoder.decodeString()
        return fromString(string) ?: error("Not a valid duration: $string")
    }

    fun fromString(string: String): Duration? {
        val splitAt = string.indexOfFirst { it.isLetter() }.takeIf { it > 0 } ?: string.length
        val value = string.take(splitAt).toDouble()
        val ext = string.drop(splitAt)
        return when (ext) {
            "t" -> value.toInt().ticks
            "s" -> value.seconds
            "m" -> value.minutes
            "h" -> value.hours
            "d" -> value.days
            "w" -> value.days * 7
            "mo" -> value.days * 31
            else -> null
        }
    }
}

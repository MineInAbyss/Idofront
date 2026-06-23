package com.mineinabyss.idofront.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Instant

object InstantAsEpochSecondsSerializer : KSerializer<Instant> {
    override val descriptor = Long.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeLong(value.epochSeconds)
    }

    override fun deserialize(decoder: Decoder): Instant {
        return Instant.fromEpochSeconds(decoder.decodeLong())
    }
}
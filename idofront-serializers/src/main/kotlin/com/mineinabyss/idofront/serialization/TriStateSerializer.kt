package com.mineinabyss.idofront.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*
import net.kyori.adventure.util.TriState

object TriStateSerializer : KSerializer<TriState> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("TriState", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: TriState) =
        encoder.encodeString(value.toString())

    override fun deserialize(decoder: Decoder): TriState =
        TriState.valueOf(decoder.decodeString().uppercase())
}

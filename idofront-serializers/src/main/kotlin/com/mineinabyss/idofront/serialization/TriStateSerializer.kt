package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.serialization.helpers.ListAsEnumSerialDescriptor
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.util.TriState

object TriStateSerializer : KSerializer<TriState> {
    override val descriptor: SerialDescriptor = ListAsEnumSerialDescriptor("net.kyori.adventure.util.TriState", listOf("true", "false", "not_set"))

    override fun serialize(encoder: Encoder, value: TriState) =
        encoder.encodeString(value.toString())

    override fun deserialize(decoder: Decoder): TriState =
        TriState.valueOf(decoder.decodeString().uppercase())
}

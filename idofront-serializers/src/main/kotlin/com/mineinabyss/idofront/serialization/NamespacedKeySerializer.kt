package com.mineinabyss.idofront.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.NamespacedKey

class NamespacedKeySerializer : KSerializer<NamespacedKey> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("NamespacedKey", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: NamespacedKey) {
        encoder.encodeString(value.asString())
    }

    override fun deserialize(decoder: Decoder): NamespacedKey {
        return NamespacedKey.fromString(decoder.decodeString())!!
    }
}

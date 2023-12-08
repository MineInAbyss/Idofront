package com.mineinabyss.idofront.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.potion.PotionType

@Serializable
@SerialName("PotionType")
private class PotionTypeSurrogate(val type: PotionType)

object PotionTypeSerializer : KSerializer<PotionType> {
    override val descriptor: SerialDescriptor = PotionTypeSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: PotionType) {
        encoder.encodeSerializableValue(PotionTypeSurrogate.serializer(), PotionTypeSurrogate(value))
    }

    override fun deserialize(decoder: Decoder): PotionType {
        val surrogate = decoder.decodeSerializableValue(PotionTypeSurrogate.serializer())
        return surrogate.type
    }
}

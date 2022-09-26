package com.mineinabyss.idofront.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.potion.PotionData
import org.bukkit.potion.PotionType

@Serializable
@SerialName("PotionData")
private class PotionDataSurrogate(
    val type: PotionType,
    val extended: Boolean,
    val upgraded: Boolean,
)

object PotionDataSerializer : KSerializer<PotionData> {
    override val descriptor: SerialDescriptor = PotionDataSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: PotionData) {
        encoder.encodeSerializableValue(PotionDataSurrogate.serializer(), PotionDataSurrogate(value.type, value.isExtended, value.isUpgraded))
    }

    override fun deserialize(decoder: Decoder): PotionData {
        val surrogate = decoder.decodeSerializableValue(PotionDataSurrogate.serializer())
        return PotionData(surrogate.type, surrogate.extended, surrogate.upgraded)
    }
}

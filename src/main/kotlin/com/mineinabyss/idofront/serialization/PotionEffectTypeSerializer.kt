package com.mineinabyss.idofront.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.potion.PotionEffectType

@Serializable
@SerialName("PotionEffectType")
private class PotionEffectTypeSurrogate(
    val type: String
) {
    init {
        require(type.isNotEmpty() && PotionEffectType.getByName(type) != null) { "PotionEffectType must be valid" }
    }
}

object PotionEffectTypeSerializer : KSerializer<PotionEffectType> {
    override val descriptor: SerialDescriptor = PotionEffectTypeSurrogate.serializer().descriptor
    override fun serialize(encoder: Encoder, value: PotionEffectType) {
        val surrogate = PotionEffectTypeSurrogate(value.name)
        encoder.encodeSerializableValue(PotionEffectTypeSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): PotionEffectType {
        val surrogate = decoder.decodeSerializableValue(PotionEffectTypeSurrogate.serializer())
        return PotionEffectType.getByName(surrogate.type)!!
    }
}

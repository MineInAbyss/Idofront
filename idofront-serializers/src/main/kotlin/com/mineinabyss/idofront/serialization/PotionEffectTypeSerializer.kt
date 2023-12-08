package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.util.toMCKey
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Registry
import org.bukkit.potion.PotionEffectType

@JvmInline
@Serializable
@SerialName("PotionEffectType")
private value class PotionEffectTypeSurrogate(val type: String) {

    init {
        require(type.isNotBlank() && Registry.POTION_EFFECT_TYPE.get(type.toMCKey()) != null) { "PotionEffectType must be valid" }
    }
}

object PotionEffectTypeSerializer : KSerializer<PotionEffectType> {
    override val descriptor: SerialDescriptor = PotionEffectTypeSurrogate.serializer().descriptor
    override fun serialize(encoder: Encoder, value: PotionEffectType) {
        val surrogate = PotionEffectTypeSurrogate(value.key.asString())
        encoder.encodeSerializableValue(PotionEffectTypeSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): PotionEffectType {
        val surrogate = decoder.decodeSerializableValue(PotionEffectTypeSurrogate.serializer())
        return Registry.POTION_EFFECT_TYPE.get(surrogate.type.toMCKey())!!
    }
}

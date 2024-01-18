package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.util.toMCKey
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Registry
import org.bukkit.potion.PotionEffectType

object PotionEffectTypeSerializer : KSerializer<PotionEffectType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("PotionEffectType", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: PotionEffectType) {
        encoder.encodeString(value.key.asString())
    }

    override fun deserialize(decoder: Decoder): PotionEffectType {
        val name = decoder.decodeString()
        val type = name.toMCKey()
        return Registry.POTION_EFFECT_TYPE.get(type) ?: throw IllegalArgumentException("Invalid potion type: $name")
    }
}

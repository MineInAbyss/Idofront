package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.util.toMCKey
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.potion.PotionType

object PotionTypeSerializer : KSerializer<PotionType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("PotionType", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: PotionType) {
        encoder.encodeString(value.key.asString())
    }

    override fun deserialize(decoder: Decoder): PotionType {
        val name = decoder.decodeString()
        val type = name.toMCKey()
        return Registry.POTION.get(type) ?: throw IllegalArgumentException("Invalid potion type: $name")
    }
}

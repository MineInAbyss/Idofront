package com.mineinabyss.idofront.serialization

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
        val type = NamespacedKey.fromString(name) ?: throw IllegalArgumentException("Invalid potion type: $name")
        return Registry.POTION.get(type) ?: throw IllegalArgumentException("Invalid potion type: $name")
    }
}

package com.mineinabyss.idofront.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Material


object MaterialByNameSerializer : KSerializer<Material> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("material", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Material) {
        encoder.encodeString(value.key.toString())
    }

    override fun deserialize(decoder: Decoder): Material {
        val name = decoder.decodeString()
        return Material.matchMaterial(name) ?: error("Material $name does not exist")
    }
}

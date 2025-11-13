package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.serialization.helpers.RegistryAsEnumSerialDescriptor
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Material
import org.bukkit.Registry


object MaterialByNameSerializer : KSerializer<Material> {
    override val descriptor: SerialDescriptor =
        RegistryAsEnumSerialDescriptor("com.mineinabyss.Material", Registry.MATERIAL)

    override fun serialize(encoder: Encoder, value: Material) {
        encoder.encodeString(value.key.toString())
    }

    override fun deserialize(decoder: Decoder): Material {
        val name = decoder.decodeString()
        return Material.matchMaterial(name) ?: error("Material $name does not exist")
    }
}

package com.mineinabyss.idofront.serialization.helpers

import com.mineinabyss.idofront.util.toMCKey
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Keyed
import org.bukkit.Registry

abstract class RegistrySerializer<T : Keyed>(
    private val serialName: String,
    private val registry: Registry<T>,
) : KSerializer<T> {
    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor = RegistryAsEnumSerialDescriptor(serialName, registry)

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeString(value.key.asString())
    }

    override fun deserialize(decoder: Decoder): T {
        val name = decoder.decodeString()
        val type = name.toMCKey()
        return registry.get(type) ?: throw IllegalArgumentException("Invalid registry entry: $name")
    }
}
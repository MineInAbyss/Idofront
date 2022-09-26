package com.mineinabyss.idofront.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.util.Vector

object VectorSerializer : KSerializer<Vector> {
    private val serializer = ListSerializer(Double.serializer())
    override val descriptor: SerialDescriptor = serializer.descriptor

    override fun serialize(encoder: Encoder, value: Vector) {
        with(value) {
            encoder.encodeSerializableValue(serializer, listOf(x, y, z))
        }
    }

    override fun deserialize(decoder: Decoder): Vector {
        val (x, y, z) = decoder.decodeSerializableValue(serializer)
        return Vector(x, y, z)
    }
}

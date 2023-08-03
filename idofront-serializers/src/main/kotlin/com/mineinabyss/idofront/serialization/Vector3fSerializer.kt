package com.mineinabyss.idofront.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.joml.Vector3f

object Vector3fSerializer : KSerializer<Vector3f> {
    private val serializer = ListSerializer(Float.serializer())
    override val descriptor: SerialDescriptor = serializer.descriptor

    override fun serialize(encoder: Encoder, value: Vector3f) {
        with(value) {
            encoder.encodeSerializableValue(serializer, listOf(x, y, z))
        }
    }

    override fun deserialize(decoder: Decoder): Vector3f {
        val (x, y, z) = decoder.decodeSerializableValue(serializer)
        return Vector3f(x, y, z)
    }
}

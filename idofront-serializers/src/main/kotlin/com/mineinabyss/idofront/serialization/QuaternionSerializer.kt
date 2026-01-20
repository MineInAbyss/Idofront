package com.mineinabyss.idofront.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getWorld
import org.bukkit.Location
import org.bukkit.util.Vector
import org.joml.Quaternionf
import org.joml.Vector3f

@Serializable
@SerialName("Quaternion")
private class QuaternionSurrogate(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f,
    val w: Float = 0f,
)

object QuaternionfSerializer : KSerializer<Quaternionf> {
    override val descriptor: SerialDescriptor = QuaternionSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Quaternionf) {
        encoder.encodeSerializableValue(QuaternionSurrogate.serializer(), QuaternionSurrogate(value.x, value.y, value.z, value.w))
    }

    override fun deserialize(decoder: Decoder): Quaternionf {
        val surrogate = decoder.decodeSerializableValue(QuaternionSurrogate.serializer())
        return Quaternionf(surrogate.x, surrogate.y, surrogate.z, surrogate.w)
    }
}

object QuaternionfAltSerializer : KSerializer<Quaternionf> {
    override val descriptor = PrimitiveSerialDescriptor("Quaternionf", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Quaternionf) {
        encoder.encodeString(buildString {
            append("${value.x},${value.y},${value.z},${value.w}")
        })
    }

    override fun deserialize(decoder: Decoder): Quaternionf {
        val string = decoder.decodeString().split(",", limit = 4).toMutableList()
        val (x, y, z, w) = (0..3).map { string.getOrNull(it)?.toFloatOrNull() }

        return Quaternionf(x ?: 0f, y ?: 0f, z ?: 0f, w ?: 1f)
    }
}

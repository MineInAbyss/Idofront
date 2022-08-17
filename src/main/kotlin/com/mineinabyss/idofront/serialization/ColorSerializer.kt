package com.mineinabyss.idofront.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Color

@Serializable
@SerialName("Color")
private class ArmorColor(
    val red: Int,
    val green: Int,
    val blue: Int,
)

object ColorSerializer : KSerializer<Color> {
    override val descriptor: SerialDescriptor = ArmorColor.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Color) {
        encoder.encodeSerializableValue(ArmorColor.serializer(), ArmorColor(value.red, value.green, value.blue))
    }

    override fun deserialize(decoder: Decoder): Color {
        val surrogate = decoder.decodeSerializableValue(ArmorColor.serializer())
        return Color.fromRGB(surrogate.red, surrogate.green, surrogate.blue)
    }
}

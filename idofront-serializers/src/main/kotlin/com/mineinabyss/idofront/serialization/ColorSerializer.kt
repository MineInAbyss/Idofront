package com.mineinabyss.idofront.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Color

@JvmInline
@Serializable
@SerialName("Color")
private value class ColorSurrogate(val color: String) {
    init {
        val split = color.splitColor()
        require(split.size == 3) { "Color must be in the form of 'r, g, b'" }
        require(split.all { it in 0..255 }) { "Color must be in the range of 0-255" }
    }
}

object ColorSerializer : KSerializer<Color> {
    override val descriptor: SerialDescriptor = ColorSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Color) {
        encoder.encodeSerializableValue(ColorSurrogate.serializer(), ColorSurrogate(value.toString()))
    }

    override fun deserialize(decoder: Decoder): Color {
        val surrogate = decoder.decodeSerializableValue(ColorSurrogate.serializer()).color.splitColor()
        return Color.fromRGB(surrogate[0], surrogate[1], surrogate[2])
    }
}

private fun String.splitColor(): List<Int> = this.replace(" ", "").split(",").mapNotNull { it.toIntOrNull() }

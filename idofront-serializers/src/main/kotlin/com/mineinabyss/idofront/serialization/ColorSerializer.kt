@file:OptIn(ExperimentalStdlibApi::class, ExperimentalStdlibApi::class)

package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.jsonschema.dsl.SchemaType
import com.mineinabyss.idofront.jsonschema.dsl.withJsonSchema
import com.mineinabyss.idofront.util.ColorHelpers
import com.mineinabyss.idofront.util.toColor
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import org.bukkit.Color

@JvmInline
@Serializable
@SerialName("Color")
private value class ColorSurrogate(val color: String) {
    init {
        val split = color.splitColor()
        when {
            color.startsWith("#") ->
                require(color.substring(1).let { it.length == 6 || it.length == 8 }) { "Color must be in the form of '#rrggbb' or '#aarrggbb, got $color"  }
            color.startsWith("0x") ->
                require(color.substring(2).let { it.length == 6 || it.length == 8 }) { "Color must be in the form of '0xrrggbb' or '0xaarrggbb, got $color" }
            ',' in color -> {
                require(split.size == 3 || split.size == 4) { "Color must be in the form of 'r, g, b' or 'a, r, g, b, got $color" }
                require(split.all { it in 0..255 }) { "Color must be in the range of 0-255, got $color" }
            }
            else -> require(color.toIntOrNull(16) != null) { "Color must be an integer, got $color" }
        }
    }
}

object ColorSerializer : KSerializer<Color> {
    override val descriptor: SerialDescriptor = ColorSurrogate.serializer().descriptor.withJsonSchema {
        type = SchemaType.STRING
        title = "Color"
        pattern = "^#[0-9a-fA-F]{6}$|^#[0-9a-fA-F]{8}$|^0x[0-9a-fA-F]{6}$|^0x[0-9a-fA-F]{8}$|^\\d{1,8},\\d{1,8},\\d{1,8}$|^\\d{1,8},\\d{1,8},\\d{1,8},\\d{1,8}$"
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun serialize(encoder: Encoder, value: Color) {
        val hex = value.asARGB().toHexString(ColorHelpers.hexFormat)
        val hexColor = if (value.alpha == 255 && hex.length > 7) hex.substring(2) else hex
        encoder.encodeSerializableValue(
            ColorSurrogate.serializer(),
            ColorSurrogate("#$hexColor")
        )
    }

    override fun deserialize(decoder: Decoder): Color {
        return decoder.decodeSerializableValue(ColorSurrogate.serializer()).color.toColor()
    }
}

private fun String.splitColor(): List<Int> = this.replace(" ", "").split(",").mapNotNull { it.toIntOrNull() }

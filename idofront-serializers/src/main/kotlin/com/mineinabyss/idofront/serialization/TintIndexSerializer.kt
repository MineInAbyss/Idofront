package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.serialization.TintSourceType.Companion.type
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Color
import team.unnamed.creative.item.tint.ConstantTintSource
import team.unnamed.creative.item.tint.CustomModelDataTintSource
import team.unnamed.creative.item.tint.KeyedAndBackedTintSource
import team.unnamed.creative.item.tint.TintSource

enum class TintSourceType {
    CONSTANT, DYE, FIREWORK, GRASS, MAP_COLOR, POTION, TEAM, CUSTOM_MODEL_DATA;

    companion object {
        val KeyedAndBackedTintSource.type: TintSourceType get() = TintSourceType.valueOf(this.key().value().uppercase())
    }
}

@Serializable
@SerialName("TintSource")
private class TintSourceSurrogate(
    val type: TintSourceType,
    val defaultColor: @Serializable(with = ColorSerializer::class) Color
)

object TintSourceSerializer : KSerializer<TintSource> {
    override val descriptor: SerialDescriptor = TintSourceSurrogate.serializer().descriptor
    override fun serialize(encoder: Encoder, value: TintSource) {
        val (type, color) = when (value) {
            is ConstantTintSource -> TintSourceType.CONSTANT to value.tint()
            is KeyedAndBackedTintSource -> value.type to value.defaultTint()
            //is GrassTintSource -> TintSourceType.GRASS to value.downfall()
            is CustomModelDataTintSource -> TintSourceType.CUSTOM_MODEL_DATA to value.defaultTint()
            else -> error("Unsupported tint source: $value")
        }
        val surrogate = TintSourceSurrogate(type, Color.fromARGB(color))
        encoder.encodeSerializableValue(TintSourceSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): TintSource {
        val surrogate = decoder.decodeSerializableValue(TintSourceSurrogate.serializer())
        return when (surrogate.type) {
            TintSourceType.CUSTOM_MODEL_DATA -> TintSource.customModelData(0, surrogate.defaultColor.asARGB())
            TintSourceType.GRASS -> TintSource.grass(1f, 1f)
            TintSourceType.DYE -> TintSource.dye(surrogate.defaultColor.asARGB())
            TintSourceType.TEAM -> TintSource.team(surrogate.defaultColor.asARGB())
            TintSourceType.POTION -> TintSource.potion(surrogate.defaultColor.asARGB())
            TintSourceType.FIREWORK -> TintSource.firework(surrogate.defaultColor.asARGB())
            TintSourceType.MAP_COLOR -> TintSource.mapColor(surrogate.defaultColor.asARGB())
            TintSourceType.CONSTANT -> TintSource.constant(surrogate.defaultColor.asARGB())
        }
    }
}
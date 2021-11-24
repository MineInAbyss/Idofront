package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.time.inWholeTicks
import com.mineinabyss.idofront.time.ticks
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.time.Duration

object PotionEffectSerializer : KSerializer<PotionEffect> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Location") {
        element<String>("type")
        element<Duration>("duration")
        element<Int>("amplifier")
        element<Boolean>("ambient")
    }

    override fun serialize(encoder: Encoder, value: PotionEffect) =
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.type.toString())
            encodeSerializableElement(descriptor, 1, DurationSerializer, value.duration.ticks)
            encodeIntElement(descriptor, 2, value.amplifier)
            encodeBooleanElement(descriptor, 3, value.isAmbient)
        }

    override fun deserialize(decoder: Decoder): PotionEffect {
        var type = ""
        var duration = 1.ticks
        var amplifier = 1
        var isAmbient = true
        decoder.decodeStructure(descriptor) {
            loop@ while (true) {
                when (val i = decodeElementIndex(descriptor)) {
                    0 -> type = decodeStringElement(descriptor, i)
                    1 -> duration = decodeSerializableElement(descriptor, i, DurationSerializer)
                    2 -> amplifier = decodeIntElement(descriptor, i)
                    3 -> isAmbient = decodeBooleanElement(descriptor, i)
                    //TODO particles
                    //TODO icon
                    CompositeDecoder.DECODE_DONE -> break
                }
            }
        }
        return PotionEffect(
            PotionEffectType.getByName(type) ?: error("$type is not a valid potion effect type"),
            duration.inWholeTicks.toInt(),
            amplifier,
            isAmbient,
        )
    }
}

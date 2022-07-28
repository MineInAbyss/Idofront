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

object PotionEffectSerializer : KSerializer<PotionEffect> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Location") {
        element<String>("type")
        element("duration", DurationSerializer.descriptor)
        element<Int>("amplifier")
        element<Boolean>("ambient")
        element<Boolean>("particles")
        element<Boolean>("icon")
    }

    override fun serialize(encoder: Encoder, value: PotionEffect) =
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.type.toString())
            encodeSerializableElement(descriptor, 1, DurationSerializer, value.duration.ticks)
            encodeIntElement(descriptor, 2, value.amplifier)
            encodeBooleanElement(descriptor, 3, value.isAmbient)
            encodeBooleanElement(descriptor, 4, value.hasParticles())
            encodeBooleanElement(descriptor, 5, value.hasIcon())
        }

    override fun deserialize(decoder: Decoder): PotionEffect {
        var type = ""
        var duration = 1.ticks
        var amplifier = 1
        var isAmbient = true
        var hasParticles = true
        var hasIcon = true
        decoder.decodeStructure(descriptor) {
            loop@ while (true) {
                when (val i = decodeElementIndex(descriptor)) {
                    0 -> type = decodeStringElement(descriptor, i)
                    1 -> duration = decodeSerializableElement(descriptor, i, DurationSerializer)
                    2 -> amplifier = decodeIntElement(descriptor, i)
                    3 -> isAmbient = decodeBooleanElement(descriptor, i)
                    4 -> hasParticles = decodeBooleanElement(descriptor, i)
                    5 -> hasIcon = decodeBooleanElement(descriptor, i)
                    CompositeDecoder.DECODE_DONE -> break
                }
            }
        }
        return PotionEffect(
            PotionEffectType.getByName(type) ?: error("$type is not a valid potion effect type"),
            duration.inWholeTicks.toInt(),
            amplifier,
            isAmbient,
            hasParticles,
            hasIcon
        )
    }
}

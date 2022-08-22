package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.time.inWholeTicks
import com.mineinabyss.idofront.time.ticks
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

@Serializable
@SerialName("PotionEffect")
private class PotionEffectSurrogate(
    val type: @Serializable(with = PotionEffectTypeSerializer::class) PotionEffectType,
    val duration: @Serializable(with = DurationSerializer::class) Duration = 1.ticks,
    val amplifier: Int = 0,
    val isAmbient: Boolean = true,
    val hasParticles: Boolean = true,
    val hasIcon: Boolean = true
) {
    init {
        require(duration > 0.seconds) { "PotionEffect must have a duration" }
    }
}

object PotionEffectSerializer : KSerializer<PotionEffect> {
    override val descriptor: SerialDescriptor = PotionEffectSurrogate.serializer().descriptor
    override fun serialize(encoder: Encoder, value: PotionEffect) {
        val surrogate = PotionEffectSurrogate(
            value.type,
            value.duration.seconds,
            value.amplifier,
            value.isAmbient,
            value.hasParticles(),
            value.hasIcon()
        )
        encoder.encodeSerializableValue(PotionEffectSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): PotionEffect {
        val surrogate = decoder.decodeSerializableValue(PotionEffectSurrogate.serializer())
        return PotionEffect(
            surrogate.type,
            surrogate.duration.inWholeTicks.seconds.toInt(DurationUnit.SECONDS),
            surrogate.amplifier,
            surrogate.isAmbient,
            surrogate.hasParticles,
            surrogate.hasIcon
        )
    }
}

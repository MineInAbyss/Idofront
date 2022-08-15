package com.mineinabyss.idofront.serialization

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

@Serializable
@SerialName("PotionEffect")
private class PotionEffectSurrogate(
    val type: String,
    val duration: @Serializable(with = DurationSerializer::class) Duration,
    val amplifier: Int = 0,
    val isAmbient: Boolean = true,
    val hasParticles: Boolean = true,
    val hasIcon: Boolean = true
) {
    init {
        require(type.isNotEmpty() && duration > 0.seconds) { "PotionEffect must have a type and a duration" }
    }
}

object PotionEffectSerializer : KSerializer<PotionEffect> {
    override val descriptor: SerialDescriptor = PotionEffectSurrogate.serializer().descriptor
    override fun serialize(encoder: Encoder, value: PotionEffect) {
        val surrogate = PotionEffectSurrogate(
            value.type.name,
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
            PotionEffectType.getByName(surrogate.type)!!,
            surrogate.duration.inWholeSeconds.toInt(),
            surrogate.amplifier,
            surrogate.isAmbient,
            surrogate.hasParticles,
            surrogate.hasIcon
        )
    }
}

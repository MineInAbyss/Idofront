package com.mineinabyss.idofront.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.bossbar.BossBar.*
import net.kyori.adventure.text.Component

@Serializable
@SerialName("BossBar")
private class BossBarSurrogate(
    @Serializable(with = MiniMessageSerializer::class)
    val name: Component,
    val progress: Float,
    val color: Color,
    val overlay: Overlay,
)

object BossBarSerializer : KSerializer<BossBar> {
    override val descriptor: SerialDescriptor = BossBarSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: BossBar) {
        encoder.encodeSerializableValue(
            BossBarSurrogate.serializer(),
            BossBarSurrogate(value.name(), value.progress(), value.color(), value.overlay())
        )
    }

    override fun deserialize(decoder: Decoder): BossBar {
        val surrogate = decoder.decodeSerializableValue(BossBarSurrogate.serializer())
        return bossBar(surrogate.name, surrogate.progress, surrogate.color, surrogate.overlay)
    }
}

package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.messaging.miniMsg
import com.mineinabyss.idofront.messaging.serialize
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.bossbar.BossBar.*
import net.kyori.adventure.text.Component

object BossBarSerializer : KSerializer<BossBar> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("BossBar") {
        element<String>("name")
        element<Float>("progress")
        element<Color>("color")
        element<Overlay>("overlay")
    }

    override fun serialize(encoder: Encoder, value: BossBar) =
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.name().serialize())
            encodeFloatElement(descriptor, 1, value.progress())
            encodeStringElement(descriptor, 2, value.color().name)
            encodeStringElement(descriptor, 3, value.overlay().name)
        }

    override fun deserialize(decoder: Decoder): BossBar {
        var name = ""
        var progress = 0f
        var color = Color.WHITE.name
        var overlay = Overlay.PROGRESS.name
        decoder.decodeStructure(descriptor) {
            loop@ while (true) {
                when (val i = decodeElementIndex(descriptor)) {
                    0 -> name = decodeStringElement(descriptor, i)
                    1 -> progress = decodeFloatElement(descriptor, i)
                    2 -> color = decodeStringElement(descriptor, i)
                    3 -> overlay = decodeStringElement(descriptor, i)
                    CompositeDecoder.DECODE_DONE -> break
                }
            }
        }
        return bossBar(name.miniMsg(), progress, Color.valueOf(color), Overlay.valueOf(overlay))
    }
}

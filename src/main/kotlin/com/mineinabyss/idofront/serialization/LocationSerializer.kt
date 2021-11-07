package com.mineinabyss.idofront.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import org.bukkit.Bukkit
import org.bukkit.Location

object LocationSerializer : KSerializer<Location> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Location") {
        element<Double>("x")
        element<Double>("y")
        element<Double>("z")
        element<String>("world")
        element<Float>("pitch")
        element<Float>("yaw")
    }

    override fun serialize(encoder: Encoder, value: Location) =
        encoder.encodeStructure(descriptor) {
            encodeDoubleElement(descriptor, 0, value.x)
            encodeDoubleElement(descriptor, 1, value.y)
            encodeDoubleElement(descriptor, 2, value.z)
            encodeStringElement(descriptor, 3, value.world?.name ?: error("No world found while serializing"))
            encodeFloatElement(descriptor, 4, value.pitch)
            encodeFloatElement(descriptor, 5, value.yaw)
        }

    override fun deserialize(decoder: Decoder): Location {
        var x = 0.0
        var y = 0.0
        var z = 0.0
        var world = ""
        var pitch = 0f
        var yaw = 0f
        decoder.decodeStructure(descriptor) {
            loop@ while (true) {
                when (val i = decodeElementIndex(descriptor)) {
                    0 -> x = decodeDoubleElement(descriptor, i)
                    1 -> y = decodeDoubleElement(descriptor, i)
                    2 -> z = decodeDoubleElement(descriptor, i)
                    3 -> world = decodeStringElement(descriptor, i)
                    4 -> pitch = decodeFloatElement(descriptor, i)
                    5 -> yaw = decodeFloatElement(descriptor, i)
                    CompositeDecoder.DECODE_DONE -> break
                }
            }
        }
        return Location(Bukkit.getWorld(world), x, y, z, yaw, pitch)
    }
}

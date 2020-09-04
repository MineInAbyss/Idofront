package com.mineinabyss.idofront.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.Vector
import java.util.*

object UUIDSerializer : KSerializer<UUID> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: UUID) =
            encoder.encodeString(value.toString())

    override fun deserialize(decoder: Decoder): UUID =
            UUID.fromString(decoder.decodeString())
}

object WorldSerializer : KSerializer<World> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("World", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: World) =
            encoder.encodeString(value.name)

    override fun deserialize(decoder: Decoder): World {
        val name = decoder.decodeString()
        return Bukkit.getWorld(name) ?: error("No world $name found")
    }
}

object VectorSerializer : KSerializer<Vector> {
    private val serializer = ListSerializer(Double.serializer())
    override val descriptor: SerialDescriptor = serializer.descriptor

    override fun serialize(encoder: Encoder, value: Vector) {
        with(value) {
            encoder.encodeSerializableValue(serializer, listOf(x, y, z))
        }
    }

    override fun deserialize(decoder: Decoder): Vector {
        val (x, y, z) = decoder.decodeSerializableValue(serializer)
        return Vector(x, y, z)
    }
}

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
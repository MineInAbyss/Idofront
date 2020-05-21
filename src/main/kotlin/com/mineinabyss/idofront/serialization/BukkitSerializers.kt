package com.mineinabyss.idofront.serialization

import kotlinx.serialization.*
import kotlinx.serialization.json.JsonElementSerializer
import org.bukkit.Bukkit
import org.bukkit.Location
import java.util.*

@Serializer(forClass = UUID::class)
object UUIDSerializer : KSerializer<UUID> {
    override val descriptor: SerialDescriptor =
            PrimitiveDescriptor("UUID", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: UUID) =
            encoder.encodeString(value.toString())

    override fun deserialize(decoder: Decoder): UUID =
            UUID.fromString(decoder.decodeString())
}

@Serializer(forClass = UUID::class)
object LocationSerializer : KSerializer<Location> {
    @ImplicitReflectionSerializer
    override val descriptor: SerialDescriptor = SerialDescriptor("Location"){
        element<Double>("x")
        element<Double>("y")
        element<Double>("z")
        element<String>("world")
        element<Float>("pitch")
        element<Float>("yaw")
    }

    @ImplicitReflectionSerializer
    override fun serialize(encoder: Encoder, value: Location) =
            encoder.encodeStructure(descriptor){
                encodeDoubleElement(descriptor, 0, value.x)
                encodeDoubleElement(descriptor, 1, value.y)
                encodeDoubleElement(descriptor, 2, value.z)
                encodeStringElement(descriptor, 3, value.world?.name ?: error("No world found while serializing"))
                encodeFloatElement(descriptor, 4, value.pitch)
                encodeFloatElement(descriptor, 5, value.yaw)
            }

    @ImplicitReflectionSerializer
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
                    CompositeDecoder.READ_DONE -> break@loop
                    0 -> x = decodeDoubleElement(descriptor, i)
                    1 -> y = decodeDoubleElement(descriptor, i)
                    2 -> z = decodeDoubleElement(descriptor, i)
                    3 -> world = decodeStringElement(descriptor, i)
                    4 -> pitch = decodeFloatElement(descriptor, i)
                    5 -> yaw = decodeFloatElement(descriptor, i)
                }
            }
        }
        return Location(Bukkit.getWorld(world), x, y, z, yaw, pitch)
    }
}
package com.mineinabyss.idofront.serialization

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.joml.Vector3f

private val defaultWorld: World get() {
    return Bukkit.getWorlds()[0] ?: Bukkit.getWorld("world") ?: error("Default world not found not found. Specify world explicitly.")
}

@Serializable
@SerialName("Location")
private class LocationSurrogate(
    @EncodeDefault(EncodeDefault.Mode.NEVER) val world: @Serializable(WorldSerializer::class) World = defaultWorld,
    val x: Double,
    val y: Double,
    val z: Double,
    @EncodeDefault(EncodeDefault.Mode.NEVER) val yaw: Float = 0f,
    @EncodeDefault(EncodeDefault.Mode.NEVER) val pitch: Float = 0f
) {
    init {
        require(world in Bukkit.getWorlds())
    }
}

object LocationSerializer : KSerializer<Location> {
    override val descriptor: SerialDescriptor = LocationSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Location) {
        encoder.encodeSerializableValue(LocationSurrogate.serializer(), LocationSurrogate(value.world, value.x, value.y, value.z, value.yaw, value.pitch))
    }

    override fun deserialize(decoder: Decoder): Location {
        val surrogate = decoder.decodeSerializableValue(LocationSurrogate.serializer())
        return Location(surrogate.world, surrogate.x, surrogate.y, surrogate.z, surrogate.yaw, surrogate.pitch)
    }
}

object LocationAltSerializer : KSerializer<Location> {
    override val descriptor = PrimitiveSerialDescriptor("Location", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Location) {
        encoder.encodeString(buildString {
            append("${value.x},${value.y},${value.z}")
            if (value.yaw != 0f || value.pitch != 0f) append(" ${value.yaw},${value.pitch}")
            if (value.world.name != defaultWorld.name) append(" ${value.world.name}")
        })
    }

    // location: x,y,z yaw,pitch world
    override fun deserialize(decoder: Decoder): Location {
        val string = decoder.decodeString()
        val (x,y,z) = string.substringBefore(" ").split(",").map { it.toDoubleOrNull() ?: 0.0 }
        val (yaw, pitch) = string.substringAfter(" ").substringBeforeLast(" ").split(",").map { it.toFloatOrNull() ?: 0.0f }
        val world = string.substringAfterLast(" ").let(Bukkit::getWorld) ?: defaultWorld

        return Location(world, x, y, z, yaw, pitch)
    }
}

package com.mineinabyss.idofront.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World

@Serializable
@SerialName("Location")
private class LocationSurrogate(
    val world: @Serializable(WorldSerializer::class) World = Bukkit.getWorld("world") ?: error("World 'world' not found. Specify world explicitly."),
    val x: Double,
    val y: Double,
    val z: Double,
) {
    init {
        require(world in Bukkit.getWorlds())
    }
}

object LocationSerializer : KSerializer<Location> {
    override val descriptor: SerialDescriptor = LocationSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Location) {
        encoder.encodeSerializableValue(LocationSurrogate.serializer(), LocationSurrogate(value.world, value.x, value.y, value.z))
    }

    override fun deserialize(decoder: Decoder): Location {
        val surrogate = decoder.decodeSerializableValue(LocationSurrogate.serializer())
        return Location(surrogate.world, surrogate.x, surrogate.y, surrogate.z)
    }
}

package com.mineinabyss.idofront.serialization

import com.charleskorn.kaml.YamlInput
import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.YamlScalar
import com.mineinabyss.jsonschema.dsl.SchemaType
import com.mineinabyss.jsonschema.dsl.withJsonSchema
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World

@Serializable
@SerialName("Location")
private class LocationSurrogate(
    @EncodeDefault(EncodeDefault.Mode.NEVER) val world: @Serializable(WorldSerializer::class) World? = null,
    val x: Double,
    val y: Double,
    val z: Double,
    @EncodeDefault(EncodeDefault.Mode.NEVER) val yaw: Float = 0f,
    @EncodeDefault(EncodeDefault.Mode.NEVER) val pitch: Float = 0f,
) {
    init {
        require(world in Bukkit.getWorlds())
    }
}

object LocationSerializer : KSerializer<Location> {
    override val descriptor: SerialDescriptor = ContextualSerializer(Location::class).descriptor.withJsonSchema {
        anyOf(
            { type = SchemaType.STRING },
            {
                TODO("Provide api for referencing another serializer and auto-registering it")
                ref = $$"#/$defs/$${LocationSurrogate.serializer().descriptor.serialName}"
            }
        )
    }

    override fun serialize(encoder: Encoder, value: Location) {
        encoder.encodeSerializableValue(LocationSurrogate.serializer(), LocationSurrogate(value.world, value.x, value.y, value.z, value.yaw, value.pitch))
    }

    override fun deserialize(decoder: Decoder): Location {
        when (val node = (decoder as YamlInput).node) {
            is YamlMap -> {
                val surrogate = decoder.decodeSerializableValue(LocationSurrogate.serializer())
                return Location(surrogate.world, surrogate.x, surrogate.y, surrogate.z, surrogate.yaw, surrogate.pitch)
            }

            is YamlScalar -> {
                return decodeLocationAsString(node.content)
            }

            else -> error("Location must either be a map or a string!")
        }
    }

    fun decodeLocationAsString(string: String): Location {
        val split = string.split(" ")
        val (x, y, z) = split.take(3)
        val pitch = split.getOrNull(3)?.toFloat() ?: 0f
        val yaw = split.getOrNull(4)?.toFloat() ?: 0f
        val world = split.find { it.startsWith("world=") }
            ?.let { Bukkit.getWorld(it.removePrefix("world=")) }

        return Location(
            world,
            x.toDouble(),
            y.toDouble(),
            z.toDouble(),
            pitch,
            yaw
        )
    }
//    fun getDefaultWorld() = Bukkit.getWorlds()[0] ?: Bukkit.getWorld("world") ?: error("Default world not found not found. Specify world explicitly.")
}

package com.mineinabyss.idofront.serialization.helpers

import com.mineinabyss.idofront.util.toMCKey
import com.mineinabyss.jsonschema.dsl.SchemaType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Keyed
import org.bukkit.Registry

/**
 * @param registry Only read when deserializing, so the descriptor and schema can be built without a server
 * @param knownKeys Keys suggested in the schema, see [typedKeyNames]
 */
abstract class RegistrySerializer<T : Keyed>(
    serialName: String,
    registry: () -> Registry<T>,
    knownKeys: () -> Collection<String> = { emptyList() },
) : KSerializer<T> {
    constructor(serialName: String, registry: Registry<T>) : this(serialName, { registry })

    private val registry by lazy(registry)

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(serialName, PrimitiveKind.STRING).withSchema {
        suggestKeys(knownKeys()) {
            type = SchemaType.STRING
            pattern = NAMESPACED_KEY_PATTERN
        }
    }

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeString(value.key.asString())
    }

    override fun deserialize(decoder: Decoder): T {
        val name = decoder.decodeString()
        val type = name.toMCKey()
        return registry.get(type) ?: throw IllegalArgumentException("Invalid registry entry: $name")
    }
}

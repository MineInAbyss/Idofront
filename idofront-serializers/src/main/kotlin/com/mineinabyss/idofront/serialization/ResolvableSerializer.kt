package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.services.Resolvable
import com.mineinabyss.idofront.time.inWholeTicks
import com.mineinabyss.jsonschema.dsl.JsonSchemaDescriptor
import com.mineinabyss.jsonschema.dsl.SchemaContext
import com.mineinabyss.jsonschema.dsl.SchemaProperty
import com.mineinabyss.jsonschema.dsl.SchemaType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.key.Key
import kotlin.time.Duration

/**
 * A number is a constant, anything else is the key of an entry
 * in the context_int_provider or context_float_provider registry
 */
object ResolvableSerializer : KSerializer<Resolvable>, JsonSchemaDescriptor {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.mineinabyss.ResolvableSerializer", PrimitiveKind.STRING)

    context(context: SchemaContext)
    override fun SchemaProperty.defineSchema() {
        type = SchemaType.STRING
        title = "Resolvable"
        description = "A number, or the key of a context provider that resolves to one"
    }

    override fun serialize(encoder: Encoder, value: Resolvable) = encoder.encodeString(
        when (value) {
            is Resolvable.Constant -> value.value.toString()
            is Resolvable.Provider -> value.key.asString()
        }
    )

    override fun deserialize(decoder: Decoder): Resolvable {
        val string = decoder.decodeString()
        return string.toDoubleOrNull()?.let(Resolvable::Constant) ?: Resolvable.Provider(Key.key(string))
    }
}

/**
 * A [Resolvable] whose constant is a tick count, written either as a bare number of ticks or as a duration.
 * Durations are parsed before keys since something like `80s` is also a valid [Key] string
 */
object ResolvableDurationSerializer : KSerializer<Resolvable>, JsonSchemaDescriptor {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.mineinabyss.ResolvableDurationSerializer", PrimitiveKind.STRING)

    context(context: SchemaContext)
    override fun SchemaProperty.defineSchema() {
        type = SchemaType.STRING
        title = "Resolvable duration"
        description = "A duration, a plain number of ticks, or the key of a context provider that resolves to ticks"
    }

    override fun serialize(encoder: Encoder, value: Resolvable) = encoder.encodeString(
        when (value) {
            is Resolvable.Constant -> "${value.value.toLong()}t"
            is Resolvable.Provider -> value.key.asString()
        }
    )

    override fun deserialize(decoder: Decoder): Resolvable {
        val string = decoder.decodeString()
        val duration = (string.toDoubleOrNull() ?: (Duration.parseOrNull(string) ?: DurationSerializer.fromString(string))?.inWholeTicks?.toDouble())

        return duration?.let(Resolvable::Constant) ?: Resolvable.Provider(Key.key(string))
    }
}

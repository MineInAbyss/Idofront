package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.services.Resolvable
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

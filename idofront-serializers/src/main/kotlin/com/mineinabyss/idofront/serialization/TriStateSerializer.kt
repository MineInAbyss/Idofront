package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.serialization.helpers.ListAsEnumSerialDescriptor
import com.mineinabyss.jsonschema.dsl.JsonSchemaDescriptor
import com.mineinabyss.jsonschema.dsl.SchemaContext
import com.mineinabyss.jsonschema.dsl.SchemaProperty
import com.mineinabyss.jsonschema.dsl.SchemaType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.util.TriState

object TriStateSerializer : KSerializer<TriState>, JsonSchemaDescriptor {
    private val names = listOf("true", "false", "not_set")

    override val descriptor: SerialDescriptor = ListAsEnumSerialDescriptor("net.kyori.adventure.util.TriState", names)

    context(context: SchemaContext)
    override fun SchemaProperty.defineSchema() {
        anyOf(
            { type = SchemaType.BOOLEAN },
            {
                type = SchemaType.STRING
                enum += names + names.map { it.uppercase() }
            },
        )
    }

    override fun serialize(encoder: Encoder, value: TriState) =
        encoder.encodeString(value.toString())

    override fun deserialize(decoder: Decoder): TriState =
        TriState.valueOf(decoder.decodeString().uppercase())
}

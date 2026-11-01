package com.mineinabyss.idofront.serialization

import com.charleskorn.kaml.YamlInput
import com.charleskorn.kaml.YamlScalar
import com.mineinabyss.idofront.serialization.helpers.schemaOf
import com.mineinabyss.jsonschema.dsl.JsonSchemaDescriptor
import com.mineinabyss.jsonschema.dsl.SchemaContext
import com.mineinabyss.jsonschema.dsl.SchemaProperty
import com.mineinabyss.jsonschema.dsl.SchemaType
import kotlinx.serialization.ContextualSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Material

class SerializableItemStackSerializer : KSerializer<SerializableItemStack>, JsonSchemaDescriptor {
    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor = ContextualSerializer(BaseSerializableItemStack::class).descriptor

    context(context: SchemaContext)
    override fun SchemaProperty.defineSchema() {
        anyOf(
            {
                type = SchemaType.STRING
                description = "minecraft:<material> for a vanilla item, anything else is a prefab"
            },
            { schemaOf(BaseSerializableItemStack.serializer()) },
        )
    }

    override fun deserialize(decoder: Decoder): SerializableItemStack {
        val node = (decoder as? YamlInput)?.node ?: return BaseSerializableItemStack.serializer().deserialize(decoder)
        if (node is YamlScalar) {
            return decodeFromShorthand(node.content)
        }
        val structure = decoder.beginStructure(descriptor) as YamlInput
        val innerNode = structure.node
        if (innerNode is YamlScalar) {
            return decodeFromShorthand(innerNode.content)
        }
        return structure.decodeInline(BaseSerializableItemStack.serializer().descriptor).decodeSerializableValue(BaseSerializableItemStack.serializer())
            .also { structure.endStructure(BaseSerializableItemStack.serializer().descriptor) }

    }

    fun decodeFromShorthand(stringShorthand: String) = when {
        stringShorthand.startsWith("minecraft:") -> BaseSerializableItemStack(
            type = Material.matchMaterial(stringShorthand)
        )

        else -> {
            BaseSerializableItemStack(prefab = stringShorthand)
        }
    }

    override fun serialize(encoder: Encoder, value: SerializableItemStack) {
        BaseSerializableItemStack.serializer().serialize(encoder, value)
    }
}

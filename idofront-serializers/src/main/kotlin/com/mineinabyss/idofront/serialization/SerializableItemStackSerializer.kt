package com.mineinabyss.idofront.serialization

import com.charleskorn.kaml.YamlInput
import com.charleskorn.kaml.YamlScalar
import kotlinx.serialization.ContextualSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Material

class SerializableItemStackSerializer : KSerializer<SerializableItemStack> {
    override val descriptor = ContextualSerializer(BaseSerializableItemStack::class).descriptor

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
            material = Material.matchMaterial(stringShorthand)
        )

        else -> {
            BaseSerializableItemStack(type = stringShorthand)
        }
    }

    override fun serialize(encoder: Encoder, value: SerializableItemStack) {
        BaseSerializableItemStack.serializer().serialize(encoder, value)
    }
}

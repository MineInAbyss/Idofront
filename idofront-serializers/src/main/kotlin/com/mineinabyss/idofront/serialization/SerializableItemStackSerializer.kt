package com.mineinabyss.idofront.serialization

import com.charleskorn.kaml.YamlInput
import com.charleskorn.kaml.YamlNode
import com.charleskorn.kaml.YamlScalar
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Material

class SerializableItemStackSerializer : KSerializer<SerializableItemStack> {
    //    override val descriptor: SerialDescriptor = BaseSerializableItemStack.serializer().descriptor
    override val descriptor: SerialDescriptor = YamlNode.serializer().descriptor

    override fun deserialize(decoder: Decoder): SerializableItemStack {
        val node = (decoder as? YamlInput)?.node
        if (node is YamlScalar) {
            return decodeFromShorthand(node.content)
        }
        return decoder.decodeSerializableValue(SerializableItemStack.generatedSerializer())
    }

    fun decodeFromShorthand(stringShorthand: String) = when {
        stringShorthand.startsWith("minecraft:") -> SerializableItemStack(
            type = Material.matchMaterial(
                stringShorthand
            )
        )

        stringShorthand.startsWith("crucible ") -> SerializableItemStack(
            crucibleItem = stringShorthand.removePrefix("crucible ")
        )

        stringShorthand.startsWith("nexo ") -> SerializableItemStack(
            nexoItem = stringShorthand.removePrefix("nexo ")
        )

        else -> SerializableItemStack(prefab = stringShorthand)
    }

    override fun serialize(encoder: Encoder, value: SerializableItemStack) {
        SerializableItemStack.serializer().serialize(encoder, value)
    }
}

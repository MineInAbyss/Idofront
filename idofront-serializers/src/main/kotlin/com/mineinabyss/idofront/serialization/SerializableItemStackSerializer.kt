package com.mineinabyss.idofront.serialization

import com.charleskorn.kaml.YamlInput
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Material

class SerializableItemStackSerializer : KSerializer<SerializableItemStack> {
    //    override val descriptor: SerialDescriptor = BaseSerializableItemStack.serializer().descriptor
    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor =
        buildSerialDescriptor("SerializableItemStack", SerialKind.CONTEXTUAL)

    override fun deserialize(decoder: Decoder): SerializableItemStack {
        val composite = runCatching { (decoder as YamlInput).beginStructure(String.serializer().descriptor) }.getOrNull()
        if (composite != null) {
            val stringShorthand = composite.decodeStringElement(String.serializer().descriptor, 0)
            composite.endStructure(String.serializer().descriptor)
            when {
                stringShorthand.startsWith("minecraft:") -> return BaseSerializableItemStack(
                    type = Material.matchMaterial(
                        stringShorthand
                    )
                )

                stringShorthand.startsWith("crucible ") -> return BaseSerializableItemStack(
                    crucibleItem = stringShorthand.removePrefix("crucible ")
                )

                stringShorthand.startsWith("oraxen ") -> return BaseSerializableItemStack(
                    oraxenItem = stringShorthand.removePrefix("oraxen ")
                )

                else -> return BaseSerializableItemStack(prefab = stringShorthand)
            }

        }
        return BaseSerializableItemStack.serializer().deserialize(decoder)
    }

    override fun serialize(encoder: Encoder, value: SerializableItemStack) {
        BaseSerializableItemStack.serializer().serialize(encoder, value)
    }
}

package com.mineinabyss.idofront.serialization

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

/**
 * A serializer that accepts either a single value or a list of values.
 * Example: "a" or ["a", "b", "c"] → both deserialize to List<String>
 */
class SingleOrListSerializer<T>(
    private val elementSerializer: KSerializer<T>
) : KSerializer<List<T>> {

    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor = buildSerialDescriptor("SingleOrList", StructureKind.LIST)

    override fun serialize(encoder: Encoder, value: List<T>) {
        if (value.size == 1) {
            elementSerializer.serialize(encoder, value.first())
        } else {
            val listEncoder = encoder.beginCollection(descriptor, value.size)
            for (item in value) listEncoder.encodeSerializableElement(descriptor, 0, elementSerializer, item)
            listEncoder.endStructure(descriptor)
        }
    }

    override fun deserialize(decoder: Decoder): List<T> {
        return runCatching {
            listOf(elementSerializer.deserialize(decoder))
        }.getOrDefault(buildList {
            val dec = decoder.beginStructure(descriptor)
            var index = dec.decodeElementIndex(descriptor)
            while (index != CompositeDecoder.DECODE_DONE) {
                add(dec.decodeSerializableElement(descriptor, index, elementSerializer))
                index = dec.decodeElementIndex(descriptor)
            }
            dec.endStructure(descriptor)
        })
    }
}

package com.mineinabyss.idofront.serialization

import com.charleskorn.kaml.YamlInput
import com.charleskorn.kaml.YamlList
import com.mineinabyss.idofront.serialization.helpers.schemaOf
import com.mineinabyss.idofront.serialization.helpers.withSchema
import com.mineinabyss.jsonschema.dsl.SchemaType
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Color

/**
 * Accepts either one value or a list of values in configs.
 * The contextual descriptor keeps kaml from insisting on a list node before this serializer sees the node
 */
open class SingleOrListSerializer<T>(private val element: KSerializer<T>) : KSerializer<List<T>> {
    private val list = ListSerializer(element)

    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor =
        buildSerialDescriptor("com.mineinabyss.SingleOrList<${element.descriptor.serialName}>", SerialKind.CONTEXTUAL).withSchema {
            anyOf(
                { schemaOf(element) },
                {
                    type = SchemaType.ARRAY
                    items { schemaOf(element) }
                },
            )
        }

    override fun deserialize(decoder: Decoder): List<T> {
        val input = decoder as? YamlInput ?: return list.deserialize(decoder)
        return if (input.node is YamlList) list.deserialize(input) else listOf(element.deserialize(input))
    }

    override fun serialize(encoder: Encoder, value: List<T>) = list.serialize(encoder, value)
}

object StringsSerializer : SingleOrListSerializer<String>(String.serializer())
object BooleansSerializer : SingleOrListSerializer<Boolean>(Boolean.serializer())
object FloatsSerializer : SingleOrListSerializer<Float>(Float.serializer())
object ColorsSerializer : SingleOrListSerializer<Color>(ColorSerializer)

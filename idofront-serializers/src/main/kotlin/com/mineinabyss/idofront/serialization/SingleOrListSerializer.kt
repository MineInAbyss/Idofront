package com.mineinabyss.idofront.serialization

import com.charleskorn.kaml.YamlInput
import com.charleskorn.kaml.YamlList
import kotlinx.serialization.ContextualSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Color

/**
 * Accepts either one value or a list of values in configs.
 * The contextual descriptor keeps kaml from insisting on a list node before this serializer sees the node
 */
open class SingleOrListSerializer<T>(private val element: KSerializer<T>) : KSerializer<List<T>> {
    private val list = ListSerializer(element)

    override val descriptor: SerialDescriptor = ContextualSerializer(Any::class).descriptor

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

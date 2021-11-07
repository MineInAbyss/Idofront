package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.util.DoubleRange
import com.mineinabyss.idofront.util.FloatRange
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

abstract class RangeSerializer<T : ClosedRange<*>> : KSerializer<T> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("range", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: T) =
        encoder.encodeString(value.toStringWithSingleDigit())
}

/**
 * A serializer for [IntRange]s which parses input as `min..max`, `min to max`, or one value for both min and max.
 */
object IntRangeSerializer : RangeSerializer<IntRange>() {
    override fun deserialize(decoder: Decoder): IntRange {
        val (min, max) = valuesForRange(decoder.decodeString()) { toInt() }
        return min..max
    }
}

/**
 * A serializer for [LongRange]s which parses input as `min..max`, `min to max`, or one value for both min and max.
 */
object LongRangeSerializer : RangeSerializer<LongRange>() {
    override fun deserialize(decoder: Decoder): LongRange {
        val (min, max) = valuesForRange(decoder.decodeString()) { toLong() }
        return min..max
    }
}

/**
 * A serializer for [CharRange]s which parses input as `min..max`, `min to max`, or one value for both min and max.
 */
object CharRangeSerializer : RangeSerializer<CharRange>() {
    override fun deserialize(decoder: Decoder): CharRange {
        val (min, max) = valuesForRange(decoder.decodeString()) { get(0) }
        return min..max
    }
}

/**
 * A serializer for [DoubleRange]s which parses input as `min..max`, `min to max`, or one value for both min and max.
 */
object DoubleRangeSerializer : RangeSerializer<DoubleRange>() {
    override fun deserialize(decoder: Decoder): DoubleRange {
        val (min, max) = valuesForRange(decoder.decodeString()) { toDouble() }
        return min..max
    }
}

/**
 * A serializer for [FloatRange]s which parses input as `min..max`, `min to max`, or one value for both min and max.
 */
object FloatRangeSerializer : RangeSerializer<FloatRange>() {
    override fun deserialize(decoder: Decoder): FloatRange {
        val (min, max) = valuesForRange(decoder.decodeString()) { toFloat() }
        return min..max
    }
}

private inline fun <T> valuesForRange(string: String, map: String.() -> T): Pair<T, T> {
    val range = string.split("..", " to ").map(map)

    if (range.size > 2) error("Malformed range, $string, must follow format min..max, min to max, or one value for min/max")

    // if only one element first and last will be that one element
    return range.first() to range.last()
}

private fun ClosedRange<*>.toStringWithSingleDigit() =
    if (start == endInclusive) "$start"
    else "$start..$endInclusive"

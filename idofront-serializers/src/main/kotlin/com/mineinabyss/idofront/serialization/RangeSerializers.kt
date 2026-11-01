package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.util.DoubleRange
import com.mineinabyss.idofront.util.DurationRange
import com.mineinabyss.idofront.util.FloatRange
import com.mineinabyss.idofront.serialization.helpers.withSchema
import com.mineinabyss.jsonschema.dsl.SchemaProperty
import com.mineinabyss.jsonschema.dsl.SchemaType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

abstract class RangeSerializer<T : ClosedRange<*>>(
    serialName: String = "range",
    schema: SchemaProperty.() -> Unit = { type = SchemaType.STRING },
) : KSerializer<T> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(serialName, PrimitiveKind.STRING).withSchema { schema() }

    override fun serialize(encoder: Encoder, value: T) =
        encoder.encodeString(value.toStringWithSingleDigit())
}

/**
 * A serializer for [IntRange]s which parses input as `min..max`, `min to max`, or one value for both min and max.
 */
object IntRangeSerializer : RangeSerializer<IntRange>("com.mineinabyss.IntRange", numberRangeSchema(SchemaType.INTEGER, INTEGER)) {
    override fun deserialize(decoder: Decoder): IntRange {
        val (min, max) = valuesForRange(decoder.decodeString()) { toInt() }
        return min..max
    }
}

/**
 * A serializer for [LongRange]s which parses input as `min..max`, `min to max`, or one value for both min and max.
 */
object LongRangeSerializer : RangeSerializer<LongRange>("com.mineinabyss.LongRange", numberRangeSchema(SchemaType.INTEGER, INTEGER)) {
    override fun deserialize(decoder: Decoder): LongRange {
        val (min, max) = valuesForRange(decoder.decodeString()) { toLong() }
        return min..max
    }
}

/**
 * A serializer for [CharRange]s which parses input as `min..max`, `min to max`, or one value for both min and max.
 */
object CharRangeSerializer : RangeSerializer<CharRange>("com.mineinabyss.CharRange") {
    override fun deserialize(decoder: Decoder): CharRange {
        val (min, max) = valuesForRange(decoder.decodeString()) { get(0) }
        return min..max
    }
}

/**
 * A serializer for [DoubleRange]s which parses input as `min..max`, `min to max`, or one value for both min and max.
 */
object DoubleRangeSerializer : RangeSerializer<DoubleRange>("com.mineinabyss.DoubleRange", numberRangeSchema(SchemaType.NUMBER, DECIMAL)) {
    override fun deserialize(decoder: Decoder): DoubleRange {
        val (min, max) = valuesForRange(decoder.decodeString()) { toDouble() }
        return min..max
    }
}

/**
 * A serializer for [FloatRange]s which parses input as `min..max`, `min to max`, or one value for both min and max.
 */
object FloatRangeSerializer : RangeSerializer<FloatRange>("com.mineinabyss.FloatRange", numberRangeSchema(SchemaType.NUMBER, DECIMAL)) {
    override fun deserialize(decoder: Decoder): FloatRange {
        val (min, max) = valuesForRange(decoder.decodeString()) { toFloat() }
        return min..max
    }
}

object DurationRangeSerializer : RangeSerializer<DurationRange>("com.mineinabyss.DurationRange", {
    type = SchemaType.STRING
    pattern = "^$DURATION((\\.\\.| to |-)$DURATION)?$"
}) {
    override fun deserialize(decoder: Decoder): DurationRange {
        val (min, max) = valuesForRange(decoder.decodeString(), DurationSerializer::fromString)
        return min!!..max!!
    }
}

private const val INTEGER = "\\d+"
private const val DECIMAL = "\\d+(\\.\\d+)?"
private const val DURATION = "\\d+(\\.\\d+)?(ms|t|s|m|h|d|w|mo)"

// The - separator is only split on when the range does not start with a negative number, see valuesForRange
private fun numberRangeSchema(numberType: SchemaType, number: String): SchemaProperty.() -> Unit = {
    anyOf(
        { type = numberType },
        {
            type = SchemaType.STRING
            pattern = "^-?$number((\\.\\.| to )-?$number)?$|^$number-$number$"
        },
    )
}

private inline fun <T> valuesForRange(string: String, map: String.() -> T): Pair<T, T> {
    val range =
        (if (!string.startsWith("-")) string.split("..", " to ", "-")
        else string.split("..", " to "))
            .map(map)

    if (range.size > 2) error("Malformed range, $string, must follow format min-max, min..max, min to max, or one value for min/max")

    // if only one element first and last will be that one element
    return range.first() to range.last()
}

private fun ClosedRange<*>.toStringWithSingleDigit() =
    if (start == endInclusive) "$start"
    else "$start..$endInclusive"

package com.mineinabyss.idofront.util

import kotlin.random.Random

typealias DoubleRange = ClosedFloatingPointRange<Double>
typealias FloatRange = ClosedFloatingPointRange<Float>

/** A random value from this range's min to max, or the minimum value if max is smaller or equal to min */
fun DoubleRange.randomOrMin(): Double =
    if (start >= endInclusive) start
    else Random.nextDouble(start, endInclusive)

/** A random value from this range's min to max, or the minimum value if max is smaller or equal to min */
fun FloatRange.randomOrMin(): Float =
    if (start >= endInclusive) start
    else start + (Random.nextFloat() * (endInclusive - start))

/** A random value from this range's min to max, or the minimum value if max is smaller or equal to min */
fun IntRange.randomOrMin(): Int =
    if (start >= endInclusive) start
    else Random.nextInt(start, endInclusive)

/** A random value from this range's min to max, or the minimum value if max is smaller or equal to min */
fun LongRange.randomOrMin(): Long =
    if (start >= endInclusive) start
    else Random.nextLong(start, endInclusive)

/** Convert a string to an IntRange, with default if no valid range contained in string */
fun String.toIntRange(default: IntRange = IntRange.EMPTY): IntRange {
    val first = this.substringBefore("..").toIntOrNull() ?: default.first
    return first..(this.substringAfter("..").toIntOrNull() ?: default.last).coerceAtLeast(first)
}

/** Convert a string to an IntRange, or null if no valid range inside */
fun String.toIntRangeOrNull(): IntRange? {
    val first = this.substringBefore("..").toIntOrNull() ?: return null
    return first..(this.substringAfter("..").toIntOrNull() ?: return null)
}
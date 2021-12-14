package com.mineinabyss.idofront.font

import kotlin.math.abs

object Space {
    const val MINUS_1 = "\uF801"
    const val MINUS_2 = "\uF802"
    const val MINUS_4 = "\uF804"
    const val MINUS_8 = "\uF808"
    const val MINUS_16 = "\uF809"
    const val MINUS_32 = "\uF80A"
    const val MINUS_64 = "\uF80B"
    const val MINUS_128 = "\uF80C"
    const val MINUS_256 = "\uF80D"
    const val MINUS_512 = "\uF80E"
    const val MINUS_1024 = "\uF80F"

    const val PLUS_1 = "\uF821"
    const val PLUS_2 = "\uF822"
    const val PLUS_4 = "\uF824"
    const val PLUS_8 = "\uF828"
    const val PLUS_16 = "\uF829"
    const val PLUS_32 = "\uF82A"
    const val PLUS_64 = "\uF82B"
    const val PLUS_128 = "\uF82C"
    const val PLUS_256 = "\uF82D"
    const val PLUS_512 = "\uF82E"
    const val PLUS_1024 = "\uF82F"

    private val powers_minus = listOf(
        // Start with power of 0
        "", MINUS_1, MINUS_2, MINUS_4,
        MINUS_8, MINUS_16, MINUS_32, MINUS_64,
        MINUS_128, MINUS_256, MINUS_512, MINUS_1024
    )

    private val powers_plus = listOf(
        "", PLUS_1, PLUS_2, PLUS_4,
        PLUS_8, PLUS_16, PLUS_32, PLUS_64,
        PLUS_128, PLUS_256, PLUS_512, PLUS_1024
    )

    fun of(shift: Int) = buildString {
        val powers = if (shift > 0) powers_plus else powers_minus
        repeat(powers.size) { i ->
            val pow = i + 1
            val bit = 1 shl (i)
            if (abs(shift) and bit != 0)
                append(powers[pow])
        }
    }
}

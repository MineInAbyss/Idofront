package com.mineinabyss.idofront.font

import kotlin.math.abs

object Space {
    const val MINUS_32768 = "\uE100"
    const val MINUS_1 = "\uE101"
    const val MINUS_2 = "\uE102"
    const val MINUS_3 = "\uE103"
    const val MINUS_4 = "\uE104"
    const val MINUS_5 = "\uE105"
    const val MINUS_6 = "\uE106"
    const val MINUS_7 = "\uE107"
    const val MINUS_8 = "\uE108"
    const val MINUS_16 = "\uE109"
    const val MINUS_32 = "\uE110"
    const val MINUS_64 = "\uE111"
    const val MINUS_128 = "\uE112"
    const val MINUS_256 = "\uE113"
    const val MINUS_512 = "\uE114"
    const val MINUS_1024 = "\uE115"

    const val PLUS_32768 = "\uE116"
    const val PLUS_1 = "\uE117"
    const val PLUS_2 = "\uE118"
    const val PLUS_3 = "\uE119"
    const val PLUS_4 = "\uE120"
    const val PLUS_5 = "\uE121"
    const val PLUS_6 = "\uE122"
    const val PLUS_7 = "\uE123"
    const val PLUS_8 = "\uE124"
    const val PLUS_16 = "\uE125"
    const val PLUS_32 = "\uE126"
    const val PLUS_64 = "\uE127"
    const val PLUS_128 = "\uE128"
    const val PLUS_256 = "\uE129"
    const val PLUS_512 = "\uE130"
    const val PLUS_1024 = "\uE131"

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

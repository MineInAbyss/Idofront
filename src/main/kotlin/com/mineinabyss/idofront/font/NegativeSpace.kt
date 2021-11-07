package com.mineinabyss.idofront.font

object NegativeSpace {
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

    val powers = listOf(
        // Start with power of 0
        "", MINUS_1, MINUS_2, MINUS_4,
        MINUS_8, MINUS_16, MINUS_32, MINUS_64,
        MINUS_128, MINUS_256, MINUS_512, MINUS_1024
    )

    fun of(shift: Int) = buildString {
        repeat(powers.size) { i ->
            val pow = i + 1
            val bit = 1 shl (i)
            println(pow)
            println(bit)
            if (shift and bit != 0)
                append(powers[pow])
        }
    }
}

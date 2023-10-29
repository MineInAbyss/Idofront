package com.mineinabyss.idofront.font

import kotlin.math.abs

enum class Space(val unicode: String) {
    MINUS_0(""),
    MINUS_1("\uE101"),
    MINUS_2("\uE102"),
    MINUS_4("\uE103"),
    MINUS_8("\uE104"),
    MINUS_16("\uE105"),
    MINUS_32("\uE106"),
    MINUS_64("\uE107"),
    MINUS_128("\uE108"),
    MINUS_256("\uE109"),
    MINUS_512("\uE110"),
    MINUS_1024("\uE111"),

    PLUS_0(""),
    PLUS_1("\uE112"),
    PLUS_2("\uE113"),
    PLUS_4("\uE114"),
    PLUS_8("\uE115"),
    PLUS_16("\uE116"),
    PLUS_32("\uE117"),
    PLUS_64("\uE118"),
    PLUS_128("\uE119"),
    PLUS_256("\uE120"),
    PLUS_512("\uE121"),
    PLUS_1024("\uE122");

    override fun toString() = unicode

    companion object {
        private val powers_minus = listOf(
            // Start with power of 0
            MINUS_0, MINUS_1, MINUS_2, MINUS_4,
            MINUS_8, MINUS_16, MINUS_32, MINUS_64,
            MINUS_128, MINUS_256, MINUS_512, MINUS_1024
        )

        private val powers_plus = listOf(
            PLUS_0, PLUS_1, PLUS_2, PLUS_4,
            PLUS_8, PLUS_16, PLUS_32, PLUS_64,
            PLUS_128, PLUS_256, PLUS_512, PLUS_1024
        )

        fun Space.toNumber(): Int {
            return when (name.substringBefore("_")) {
                "PLUS" -> name.substringAfter("_").toIntOrNull() ?: 0
                "MINUS" -> -(name.substringAfter("_").toIntOrNull() ?: 0)
                else -> 0
            }
        }

        fun of(space: Int) = buildString {
            var remainingShift = space
            val sortedSpaces = (if (space > 0) powers_plus else powers_minus).sortedByDescending { abs(it.toNumber()) }

            sortedSpaces.forEach {
                while (remainingShift * it.toNumber() > 0 && abs(it.toNumber()) <= abs(remainingShift)) {
                    append(it.unicode)
                    remainingShift -= it.toNumber()
                    if (remainingShift == 0) break
                }
            }
        }
    }
}

fun space(shift: Int) = Space.of(shift)

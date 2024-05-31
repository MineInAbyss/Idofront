package com.mineinabyss.idofront.util

import java.io.Serializable

infix fun <A, B, C> Pair<A, B>.to(that: C): Triple<A, B, C> = Triple(this.first, this.second, that)
infix fun <A, B, C, D> Triple<A, B, C>.to(that: D): Quadruple<A, B, C, D> = Quadruple(this.first, this.second, this.third, that)

data class Quadruple<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
) : Serializable {

    /**
     * Returns string representation of the [Quadruple] including its [first], [second], [third] and [fourth] values.
     */
    override fun toString(): String = "($first, $second, $third, $fourth)"
}

fun <T> Quadruple<T, T, T, T>.toList(): List<T> = listOf(first, second, third, fourth)
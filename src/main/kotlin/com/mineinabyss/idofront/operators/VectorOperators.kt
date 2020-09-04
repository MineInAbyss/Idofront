package com.mineinabyss.idofront.operators

import org.bukkit.Location
import org.bukkit.util.Vector

operator fun Vector.plus(other: Vector) = add(other)

operator fun Vector.minus(other: Vector) = subtract(other)

operator fun Vector.times(other: Vector) = multiply(other)
operator fun Vector.times(by: Int) = times(by.toDouble())
operator fun Vector.times(by: Double) = multiply(by)

operator fun Vector.div(other: Vector) = divide(other)
operator fun Vector.div(by: Int) = div(by.toDouble())
operator fun Vector.div(by: Double) = multiply(1.0 / by)


operator fun Location.plus(other: Location) = add(other)
operator fun Location.plus(other: Vector) = add(other)

operator fun Location.minus(other: Location) = subtract(other)
operator fun Location.minus(other: Vector) = subtract(other)

operator fun Location.times(by: Int) = times(by.toDouble())
operator fun Location.times(by: Double) = multiply(by)

operator fun Location.div(by: Int) = div(by.toDouble())
operator fun Location.div(by: Double) = multiply(1.0 / by)
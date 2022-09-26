package com.mineinabyss.idofront.destructure

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.Vector

operator fun Location.component1(): Double = x
operator fun Location.component2(): Double = y
operator fun Location.component3(): Double = z
operator fun Location.component4(): World? = world

operator fun Vector.component1(): Double = x
operator fun Vector.component2(): Double = y
operator fun Vector.component3(): Double = z
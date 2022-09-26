package com.mineinabyss.idofront.location

import org.bukkit.Location

fun Location.findNearbyLocation(radius: Int, scale: Double = 1.0, predicate: (Location) -> Boolean): Location? {
    for (x in -radius..radius) {
        for (z in -radius..radius) {
            val checkLoc = clone().add(x * scale, 0.0, z * scale)
            if (predicate(checkLoc))
                return checkLoc
        }
    }
    return null
}

fun Location.up(by: Number): Location = add(0.0, by.toDouble(), 0.0)

fun Location.down(by: Number): Location = up(-by.toDouble())
package com.mineinabyss.idofront.util

import org.bukkit.Color

/** Converts this string to a [Color] where the string can be formatted with #, 0x and RGB split by ,*/
fun String.toColor(): Color {
    return when {
        this.startsWith("#", ignoreCase = true) || this.startsWith("0x", ignoreCase = true) -> {
            val hexValue = this.substring(if (this[1] == 'x') 2 else 1)
            val argb = when (hexValue.length) {
                6 -> 0xFF000000.toInt() or hexValue.toLong(16).toInt()
                8 -> hexValue.toLong(16).toInt()
                else -> throw IllegalArgumentException("Invalid hex color format")
            }
            Color.fromARGB(argb)
        }
        "," in this -> {
            val colorString = this.replace(" ", "").split(",")
            if (colorString.any { it.toIntOrNull() == null }) return Color.WHITE
            Color.fromRGB(colorString[0].toInt(), colorString[1].toInt(), colorString[2].toInt())
        }
        //TODO Make this support text, probably through minimessage
        else -> Color.WHITE
    }
}


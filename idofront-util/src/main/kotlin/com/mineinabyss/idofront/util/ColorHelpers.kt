package com.mineinabyss.idofront.util

import org.bukkit.Color

/** Converts this string to a [Color] where the string can be formatted with #, 0x and RGB split by ,*/
fun String.toColor(): Color {
    return when {
        this.startsWith("#", ignoreCase = true) || this.startsWith("0x", ignoreCase = true) -> {
            val hexValue = if (this.startsWith("#")) this.drop(1) else this.drop(2)
            when (hexValue.length) {
                6 -> Color.fromRGB(hexValue.toInt(16))
                8 -> Color.fromARGB(hexValue.toLong(16).toInt())
                else -> throw IllegalArgumentException("Invalid hex color format")
            }
        }
        "," in this -> {
            val colorString = this.replace(" ", "").split(",")
            when (colorString.mapNotNull { it.toIntOrNull() }.size) {
                3 -> Color.fromRGB(colorString[0].toInt(), colorString[1].toInt(), colorString[2].toInt())
                4 -> Color.fromARGB(colorString[0].toInt(), colorString[1].toInt(), colorString[2].toInt(), colorString[3].toInt())
                else -> Color.WHITE
            }
        }
        //TODO Make this support text, probably through minimessage
        else -> Color.WHITE
    }
}




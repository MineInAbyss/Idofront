@file:OptIn(ExperimentalStdlibApi::class)

package com.mineinabyss.idofront.util

import org.bukkit.Color

object ColorHelpers {
    @OptIn(ExperimentalStdlibApi::class)
    val hexFormat = HexFormat {
        this.upperCase = true
        this.number {
            this.removeLeadingZeros = false
        }
    }
}


/** Converts this string to a [Color] where the string can be formatted with #, 0x and RGB split by ,*/
fun String.toColor(): Color {
    return when {
        this.startsWith("#") -> Color.fromARGB(this.drop(1).padStart(8, 'F').hexToInt(ColorHelpers.hexFormat))
        this.startsWith("0x") -> Color.fromARGB(this.drop(2).padStart(8, 'F').hexToInt(ColorHelpers.hexFormat))
        "," in this -> {
            val colorString = this.removeSpaces().split(",")
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


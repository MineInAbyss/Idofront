@file:OptIn(ExperimentalStdlibApi::class)

package com.mineinabyss.idofront.util

import net.kyori.adventure.text.format.NamedTextColor
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
    return runCatching {
        when {
            this.startsWith("#") -> Color.fromARGB(this.drop(1).padStart(8, 'F').hexToInt(ColorHelpers.hexFormat))
            this.startsWith("0x") -> Color.fromARGB(this.drop(2).padStart(8, 'F').hexToInt(ColorHelpers.hexFormat))
            "," in this -> {
                val color = this.removeSpaces().split(",")
                when (color.mapNotNull(String::toIntOrNull).size) {
                    3 -> Color.fromRGB(color[0].toInt(), color[1].toInt(), color[2].toInt())
                    4 -> Color.fromARGB(color[0].toInt(), color[1].toInt(), color[2].toInt(), color[3].toInt())
                    else -> null
                }
            }
            else -> NamedTextColor.NAMES.value(this)?.value()?.let(Color::fromRGB)
        }
    }.getOrNull() ?: Color.WHITE
}


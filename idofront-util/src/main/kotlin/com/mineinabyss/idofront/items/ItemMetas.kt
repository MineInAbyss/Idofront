package com.mineinabyss.idofront.items

import org.bukkit.Color
import org.bukkit.inventory.meta.FireworkEffectMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.MapMeta
import org.bukkit.inventory.meta.PotionMeta

interface Colorable {
    var color: Color?
}

/**
 * These different ItemMeta classes don't share a common color property so we use this :(
 */
fun ItemMeta.asColorable(): Colorable? {
    return when (val meta = this) {
        is LeatherArmorMeta -> object : Colorable {
            override var color: Color?
                get() = meta.color
                set(value) {
                    meta.setColor(value)
                }
        }

        is PotionMeta -> object : Colorable {
            override var color: Color?
                get() = meta.color
                set(value) {
                    meta.color = value
                }
        }

        is MapMeta -> object : Colorable {
            override var color: Color?
                get() = meta.color
                set(value) {
                    meta.color = value
                }
        }

        is FireworkEffectMeta -> object : Colorable {
            override var color: Color?
                get() = meta.color
                set(value) {
                    meta.color = value
                }
        }

        else -> null
    }
}

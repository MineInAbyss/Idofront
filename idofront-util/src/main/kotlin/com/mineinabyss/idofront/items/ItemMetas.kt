package com.mineinabyss.idofront.items

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.DyedItemColor
import org.bukkit.Color
import org.bukkit.DyeColor
import org.bukkit.FireworkEffect
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.FireworkEffectMeta
import org.bukkit.inventory.meta.FireworkMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.MapMeta
import org.bukkit.inventory.meta.PotionMeta

interface Colorable {
    var color: Color?
}

fun ItemStack.asColorable(): Colorable? {
    return runCatching {
        val dyedColor = getDataOrDefault(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor().build())

        object : Colorable {
            override var color: Color?
                get() = dyedColor?.color()
                set(value) {
                    if (value == null) resetData(DataComponentTypes.DYED_COLOR)
                    else setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(value))
                }
        }
    }.getOrNull()
}

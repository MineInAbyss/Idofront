package com.mineinabyss.idofront.items

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.DyedItemColor
import org.bukkit.Color
import org.bukkit.inventory.ItemStack

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

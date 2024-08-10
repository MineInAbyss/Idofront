package com.mineinabyss.idofront.nms

import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

/**
 * In 1.20.5, HIDE_ATTIRBUTES ItemFlag has funky behaviour as the component holds the show_in_tooltip property
 * This method properly hides the Attributes from the tooltip if an item has the flag set
 */
fun ItemStack.hideAttributeTooltipWithItemFlagSet() = editMeta { meta ->
    if (!hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)) return@editMeta
    if (meta.attributeModifiers?.isEmpty == false) return@editMeta
    if (type.defaultAttributeModifiers != meta.attributeModifiers) return@editMeta

    meta.attributeModifiers = type.defaultAttributeModifiers
}
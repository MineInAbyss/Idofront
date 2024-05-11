package com.mineinabyss.idofront.items

import net.minecraft.core.component.DataComponents
import org.bukkit.craftbukkit.inventory.CraftItemStack
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

/**
 * Edits item meta if it exists, same rules as [ItemStack.editMeta]
 */
inline fun ItemStack.editItemMeta(apply: ItemMeta.() -> Unit): ItemStack {
    val meta = itemMeta ?: return this
    apply(meta)
    itemMeta = meta
    return this
}

/** Edits item meta if it is an instance of [T] */
@JvmName("editItemMetaCast")
inline fun <reified T : ItemMeta> ItemStack.editItemMeta(apply: T.() -> Unit): ItemStack {
    val meta = itemMeta as? T ?: return this
    apply(meta)
    itemMeta = meta
    return this
}

/**
 * In 1.20.5, HIDE_ATTIRBUTES ItemFlag has funky behaviour as the component holds the show_in_tooltip property
 * This method properly hides the Attributes from the tooltip if an item has the flag set
 */
fun ItemStack.hideAttributeTooltipWithItemFlagSet(): ItemStack {
    if (!hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)) return this

    return CraftItemStack.asCraftCopy(this).apply {
        handle.set(DataComponents.ATTRIBUTE_MODIFIERS, (handle.get(DataComponents.ATTRIBUTE_MODIFIERS) ?: handle.item.defaultAttributeModifiers).withTooltip(false))
    }
}

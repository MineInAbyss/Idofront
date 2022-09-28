package com.mineinabyss.idofront.items

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

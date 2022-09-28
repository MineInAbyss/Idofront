package com.mineinabyss.idofront.items

import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

/**
 * (Kotlin) Allows editing of an [ItemStack]'s [ItemMeta] e.x:
 *
 * ```
 * itemStack.editItemMeta{
 *  isUnbreakable = true
 *  setDisplayName("Custom name")
 * }
 * ```
 */
inline fun <reified T : ItemMeta> ItemStack.editItemMeta(apply: T.() -> Unit): ItemStack {
    val meta = itemMeta as? T ?: return this
    apply(meta)
    itemMeta = meta
    return this
}

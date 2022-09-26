@file:JvmName("Items")

package com.mineinabyss.idofront.items

import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
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
inline fun ItemStack.editItemMeta(apply: ItemMeta.() -> Unit): ItemStack {
    val meta = itemMeta ?: return this
    apply(meta)
    itemMeta = meta
    return this
}

/**
 * The damage value of the item
 */
var ItemStack.damage: Int?
    get() = itemMeta?.damage
    set(value) {
        if (value != null) itemMeta?.damage = value
    }

/**
 * The damage value of the item. Not nullable, be careful and ensure the item is damageable
 */
var ItemMeta.damage
    get() = (this as Damageable).damage
    set(value) {
        if (this is Damageable) damage = value
    }

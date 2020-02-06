@file:JvmName("Items")

package com.mineinabyss.idofront.items

import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta

/**
 * (Kotlin) Allows editing of an [ItemStack]'s [ItemMeta] e.x:
 *
 * ```
 * itemStack.com.mineinabyss.idofront.items.editItemMeta{
 *  it.isUnbreakable = true
 *  it.setDisplayName("Custom name")
 * }
 * ```
 */
fun ItemStack.editItemMeta(edits: (ItemMeta) -> Unit): ItemStack {
    val meta = this.itemMeta ?: return this
    edits(meta)
    this.itemMeta = meta
    return this
}

/**
 * The damage value of the item
 */
var ItemStack.damage: Int?
    get() = this.itemMeta?.damage
    set(value) {
        this.itemMeta?.damage = value!!
    }

/**
 * The damage value of the item. Not nullable, be careful and ensure the item is damageable
 */
var ItemMeta.damage
    get() = (this as Damageable).damage
    set(value) {
        if (this is Damageable) this.damage = value
    }
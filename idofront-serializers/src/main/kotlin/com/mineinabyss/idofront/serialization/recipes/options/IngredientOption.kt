package com.mineinabyss.idofront.serialization.recipes.options

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable

@Serializable
sealed class IngredientOption {
    abstract fun onCrafted(item: ItemStack, setItemInSlot: (ItemStack?) -> Unit)

    @Serializable
    @SerialName("keep")
    data object Keep : IngredientOption() {
        override fun onCrafted(item: ItemStack, setItemInSlot: (ItemStack?) -> Unit) {
            setItemInSlot(item)
        }
    }

    @Serializable
    @SerialName("damage")
    data class Damage(val amount: Int) : IngredientOption() {
        override fun onCrafted(item: ItemStack, setItemInSlot: (ItemStack?) -> Unit) {
            item.editMeta {
                if (it is Damageable) it.damage += amount
            }
            setItemInSlot(item)
        }
    }
}

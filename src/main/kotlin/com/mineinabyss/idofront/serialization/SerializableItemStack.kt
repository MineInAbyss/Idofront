package com.mineinabyss.idofront.serialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable

/**
 * A wrapper for [ItemStack] that uses [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization).
 * Allows for easy-to-use serialization to JSON (or YAML with kaml).
 *
 * Currently missing many things spigot's item serialization contains, but way cleaner to use!
 */
//TODO this should be a custom serializer, not wrapper
@Serializable
data class SerializableItemStack(
        @SerialName("type") var type: Material,
        @SerialName("amount") var amount: Int = 1,
        @SerialName("custom-model-data") var customModelData: Int? = null,
        @SerialName("display-name") var displayName: String? = null,
        @SerialName("localized-name") var localizedName: String? = null,
        @SerialName("unbreakable") var unbreakable: Boolean? = null,
        @SerialName("lore") var lore: List<String>? = null,
        @SerialName("damage") var damage: Int? = null
) {
    fun toItemStack() = ItemStack(type).apply {
        val meta = itemMeta ?: return@apply
        meta.setCustomModelData(customModelData)
        meta.setDisplayName(displayName)
        meta.setLocalizedName(localizedName)
        unbreakable?.let { meta.isUnbreakable = it }
        meta.lore = lore
        if (this is Damageable) {
            this@SerializableItemStack.damage?.let { damage = it }
        }
        this.itemMeta = meta
    }
}

/**
 * Converts an [ItemStack] to [SerializableItemStack]
 *
 * @see SerializableItemStack
 */
fun ItemStack.toSerializable(): SerializableItemStack {
    return with(itemMeta) {
        SerializableItemStack(
                type,
                amount,
                if (this?.hasCustomModelData() == true) this.customModelData else null,
                this?.displayName,
                this?.localizedName,
                this?.isUnbreakable,
                this?.lore,
                (this as? Damageable)?.damage
        )
    }
}
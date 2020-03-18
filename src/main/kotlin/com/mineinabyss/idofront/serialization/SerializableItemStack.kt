package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.items.editItemMeta
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
@Serializable
data class SerializableItemStack(
        @SerialName("type") private val _type: Material,
        @SerialName("amount") private val _amount: Int = 1,
        @SerialName("custom-model-data") private val _customModelData: Int? = null,
        @SerialName("display-name") private val _displayName: String? = null,
        @SerialName("localized-name") private val _localizedName: String? = null,
        @SerialName("unbreakable") private val _unbreakable: Boolean? = null,
        @SerialName("lore") private val _lore: List<String>? = null,
        @SerialName("damage") private val _damage: Int? = null
) {
    fun toItemStack() = ItemStack(_type).editItemMeta {
        setCustomModelData(_customModelData)
        setDisplayName(_displayName)
        setLocalizedName(_localizedName)
        _unbreakable?.let { isUnbreakable = it }
        lore = _lore
        if (this is Damageable) {
            _damage?.let { damage = it }
        }
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
                this?.customModelData,
                this?.displayName,
                this?.localizedName,
                this?.isUnbreakable,
                this?.lore,
                (this as? Damageable)?.damage
        )
    }
}
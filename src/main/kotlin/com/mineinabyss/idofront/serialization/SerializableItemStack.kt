package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.plugin.getService
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable

/**
 * A wrapper for [ItemStack] that uses [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization).
 * Allows for easy-to-use serialization to JSON (or YAML with kaml).
 *
 * Currently missing many things spigot's item serialization contains, but way cleaner to use!
 */
@Serializable
open class SerializableItemStack(
    var type: Material? = null,
    var amount: Int = 1,
    @SerialName("custom-model-data") var customModelData: Int? = null,
    @SerialName("display-name") var displayName: String? = null,
    @SerialName("localized-name") var localizedName: String? = null,
    var unbreakable: Boolean? = null,
    var lore: String? = null,
    var damage: Int? = null,
    var prefab: String? = null,
    var hideItemFlags: Boolean = false,
) {
    open fun toItemStack(applyTo: ItemStack = ItemStack(type ?: Material.AIR)) = applyTo.apply {
        prefab?.let {
            val prefabItem = prefabService.prefabToItem(it) ?: return@let
            type = prefabItem.type
            itemMeta = prefabItem.itemMeta
        }
        val meta = itemMeta ?: return@apply

        this@SerializableItemStack.type?.let { type = it }
        customModelData?.let { meta.setCustomModelData(it) }
        displayName?.let { meta.setDisplayName(it) }
        localizedName?.let { meta.setLocalizedName(it) }
        unbreakable?.let { meta.isUnbreakable = it }
        this@SerializableItemStack.lore?.let { meta.lore = it.split("\n") }
        if (this is Damageable) this@SerializableItemStack.damage?.let { damage = it }
        if (hideItemFlags) meta.removeItemFlags()

        itemMeta = meta
    }

    companion object {
        private val prefabService by lazy { getService<SerializablePrefabItemService>() }
    }
}

/**
 * Somewhat hacky service for Geary support
 * If registered, allows serializing Geary prefab items.
 */
interface SerializablePrefabItemService {
    fun prefabToItem(prefabName: String): ItemStack?
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
            this?.lore?.joinToString(separator = "\n"),
            (this as? Damageable)?.damage
        )
    }
}

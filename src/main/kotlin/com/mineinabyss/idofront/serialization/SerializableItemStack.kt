package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.plugin.getServiceViaClassNameOrNull
import kotlinx.serialization.Serializable
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.w3c.dom.css.RGBColor

/**
 * A wrapper for [ItemStack] that uses [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization).
 * Allows for easy-to-use serialization to JSON (or YAML with kaml).
 *
 * Currently missing many things spigot's item serialization contains, but way cleaner to use!
 */
@Serializable
data class SerializableItemStack(
    val type: Material? = null,
    val amount: Int? = null,
    val customModelData: Int? = null,
    val displayName: String? = null,
    val localizedName: String? = null,
    val unbreakable: Boolean? = null,
    val lore: String? = null,
    val damage: Int? = null,
    val prefab: String? = null,
    val hideItemFlags: List<ItemFlag> = listOf(),
    val armorColor: Int? = null,
    val tag: String = ""
) {
    /**
     * Converts this serialized item's data to an [ItemStack], optionally applying the changes to an
     * [existing item][applyTo].
     */
    fun toItemStack(applyTo: ItemStack = ItemStack(type ?: Material.AIR)): ItemStack {
        val meta = applyTo.itemMeta
        updateMeta(applyTo, meta)
        applyTo.itemMeta = meta
        return applyTo
    }

    /**
     * Updates the given [item] immediately (ex. material and amount), but does not set its meta.
     * Instead, only modifies it to what it *should* be by updating [meta], which the user must set on
     * the item themselves.
     *
     * This is useful to avoid unnecessary reads and writes of all the item meta which is very slow.
     */
    fun updateMeta(
        item: ItemStack,
        meta: ItemMeta
    ) {
        // Support for our prefab system in geary.
        prefab?.let { encodePrefab(item, meta, it) }

        // Modify item
        amount?.let { item.amount = it }
        type?.let { item.type = it }

        // Modify meta
        customModelData?.let { meta.setCustomModelData(it) }
        displayName?.let { meta.setDisplayName(it) }
        localizedName?.let { meta.setLocalizedName(it) }
        unbreakable?.let { meta.isUnbreakable = it }
        lore?.let { meta.lore = it.split("\n") }
        if (meta is Damageable) this@SerializableItemStack.damage?.let { meta.damage = it }
        if (hideItemFlags.isNotEmpty()) meta.addItemFlags(*hideItemFlags.toTypedArray())
        if (armorColor != null) (meta as LeatherArmorMeta).setColor(Color.fromRGB(armorColor))
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        private val encodePrefab by lazy {
            getServiceViaClassNameOrNull<SerializablePrefabItemService>() as (ItemStack, ItemMeta, String) -> Unit
        }
    }
}

/**
 * Somewhat hacky service for Geary support.
 * If registered, allows serializing Geary prefab items.
 */
// We extend a Kotlin function literal since we share Kotlin across all our plugins, but not this interface (Idofront is shaded)
interface SerializablePrefabItemService : (ItemStack, ItemMeta, String) -> Unit {
    override fun invoke(item: ItemStack, meta: ItemMeta, prefabName: String) = encodeFromPrefab(item, meta, prefabName)

    fun encodeFromPrefab(item: ItemStack, meta: ItemMeta, prefabName: String)
}

/**
 * Converts an [ItemStack] to [SerializableItemStack]
 *
 * @see SerializableItemStack
 */
fun ItemStack.toSerializable(): SerializableItemStack {
    return with(itemMeta) {
        SerializableItemStack(
            type = type,
            amount = amount,
            customModelData = if (this?.hasCustomModelData() == true) this.customModelData else null,
            displayName = this?.displayName,
            localizedName = this?.localizedName,
            unbreakable = this?.isUnbreakable,
            lore = this?.lore?.joinToString(separator = "\n"),
            damage = (this as? Damageable)?.damage,
            armorColor = (this as? LeatherArmorMeta)?.color?.asRGB()
        ) //TODO perhaps this should encode prefab too?
    }
}

@file:UseSerializers(MiniMessageSerializer::class)

package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.plugin.getServiceViaClassNameOrNull
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.LeatherArmorMeta

/**
 * A wrapper for [ItemStack] that uses [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization).
 * Allows for easy-to-use serialization to JSON (or YAML with kaml).
 *
 * Currently missing many things spigot's item serialization contains, but way cleaner to use!
 *
 * See [MiniMessage docs](https://docs.adventure.kyori.net/minimessage/format.html) for formatting info!
 */
@Serializable
data class SerializableItemStack(
    val type: Material? = null,
    val amount: Int? = null,
    val customModelData: Int? = null,
    val displayName: Component? = null,
    val lore: List<Component>? = null,
    val unbreakable: Boolean? = null,
    val damage: Int? = null,
    val prefab: String? = null,
    val itemFlags: List<ItemFlag> = listOf(),
    val attributeModifiers: List<SerializableAttribute> = listOf(),
    val color: @Serializable(with = ColorSerializer::class) Color? = null,
    val tag: String = ""
) {
    fun Component.removeItalics() =
        Component.text().decoration(TextDecoration.ITALIC, false).build().append(this)

    /**
     * Converts this serialized item's data to an [ItemStack], optionally applying the changes to an
     * [existing item][applyTo].
     */
    fun toItemStack(applyTo: ItemStack = ItemStack(type ?: Material.AIR)): ItemStack {
        // Support for our prefab system in geary.
        prefab?.let { encodePrefab(applyTo, it) }

        // Modify item
        amount?.let { applyTo.amount = it }
        type?.let { applyTo.type = it }

        // Modify meta
        val meta = applyTo.itemMeta ?: return applyTo
        customModelData?.let { meta.setCustomModelData(it) }
        displayName?.let { meta.displayName(it.removeItalics()) }
        unbreakable?.let { meta.isUnbreakable = it }
        lore?.let { meta.lore(it.map { line -> line.removeItalics() }) }
        if (meta is Damageable) this@SerializableItemStack.damage?.let { meta.damage = it }
        if (itemFlags.isNotEmpty()) meta.addItemFlags(*itemFlags.toTypedArray())
        if (color != null) (meta as? LeatherArmorMeta)?.setColor(color)
        if (attributeModifiers.isNotEmpty()) {
            meta.attributeModifiers?.forEach { attribute, modifier ->
                meta.removeAttributeModifier(attribute, modifier)
            }
            attributeModifiers.forEach { (attribute, modifier) ->
                meta.addAttributeModifier(attribute, modifier)
            }
        }
        applyTo.itemMeta = meta
        return applyTo
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        private val encodePrefab by lazy {
            getServiceViaClassNameOrNull<SerializablePrefabItemService>() as (ItemStack, String) -> Unit
        }
    }
}

/**
 * Converts an [ItemStack] to [SerializableItemStack]
 *
 * @see SerializableItemStack
 */
fun ItemStack.toSerializable(): SerializableItemStack = with(itemMeta) {
    val attributeList = mutableListOf<SerializableAttribute>()
    this.attributeModifiers?.forEach { a, m -> attributeList.add(SerializableAttribute(a, m)) }
    SerializableItemStack(
        type = type,
        amount = amount,
        customModelData = if (this?.hasCustomModelData() == true) this.customModelData else null,
        displayName = this?.displayName(),
        unbreakable = this?.isUnbreakable,
        lore = this?.lore(),
        damage = (this as? Damageable)?.damage,
        itemFlags = this?.itemFlags?.toList() ?: listOf(),
        attributeModifiers = attributeList,
        color = (this as? LeatherArmorMeta)?.color
    ) //TODO perhaps this should encode prefab too?
}

@file:UseSerializers(MiniMessageSerializer::class)

package com.mineinabyss.idofront.serialization

import com.google.common.collect.HashMultimap
import com.mineinabyss.idofront.messaging.logWarn
import com.mineinabyss.idofront.plugin.Plugins
import com.mineinabyss.idofront.plugin.Services
import dev.lone.itemsadder.api.CustomStack
import io.lumine.mythiccrucible.MythicCrucible
import io.th0rgal.oraxen.OraxenPlugin
import io.th0rgal.oraxen.api.OraxenItems
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.KnowledgeBookMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionData
import java.util.*

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
    val enchantments: List<SerializableEnchantment>? = null,
    val itemFlags: List<ItemFlag> = listOf(),
    val attributeModifiers: List<SerializableAttribute>? = null,
    val potionData: @Serializable(with = PotionDataSerializer::class) PotionData? = null,
    val knowledgeBookRecipes: List<String>? = null,
    val color: @Serializable(with = ColorSerializer::class) Color? = null,
    val tag: String = "",
    val crucibleItem: String? = null,
    val oraxenItem: String? = null,
    val itemsadderItem: String? = null,
) {
    private fun Component.removeItalics() =
        Component.text().decoration(TextDecoration.ITALIC, false).build().append(this)

    /**
     * Converts this serialized item's data to an [ItemStack], optionally applying the changes to an
     * [existing item][applyTo].
     */
    fun toItemStack(
        applyTo: ItemStack = ItemStack(type ?: Material.AIR),
        ignoreProperties: EnumSet<Properties> = EnumSet.noneOf(Properties::class.java)
    ): ItemStack {
        // Import ItemStack from Crucible
        crucibleItem?.let { id ->
            if (Plugins.isEnabled<MythicCrucible>()) {
                MythicCrucible.core().itemManager.getItemStack(id)?.let {
                    applyTo.type = it.type
                    applyTo.itemMeta = it.itemMeta
                } ?: logWarn("No Crucible item found with id $id")
            } else {
                logWarn("Tried to import Crucible item, but MythicCrucible was not enabled")
            }
        }

        // Import ItemStack from Oraxen
        oraxenItem?.let { id ->
            if (Plugins.isEnabled<OraxenPlugin>()) {
                OraxenItems.getItemById(id)?.build()?.let {
                    applyTo.type = it.type
                    applyTo.itemMeta = it.itemMeta
                } ?: logWarn("No Oraxen item found with id $id")
            } else {
                logWarn("Tried to import Oraxen item, but Oraxen was not enabled")
            }
        }

        // Import ItemStack from ItemsAdder
        itemsadderItem?.let { id ->
            if (Plugins.isEnabled("ItemsAdder")) {
                CustomStack.getInstance(id)?.itemStack?.let {
                    applyTo.type = it.type
                    applyTo.itemMeta = it.itemMeta
                } ?: logWarn("No ItemsAdder item found with id $id")
            } else {
                logWarn("Tried to import ItemsAdder item, but ItemsAdder was not enabled")
            }
        }

        // Support for our prefab system in geary.
        prefab?.takeIf { Properties.PREFAB !in ignoreProperties }?.let {
            encodePrefab?.invoke(applyTo, it)
                ?: logWarn("Tried to use prefab tag when reading item, but no prefab provider was registered")
        }

        // Modify item
        amount?.takeIf { Properties.AMOUNT !in ignoreProperties }?.let { applyTo.amount = it }
        type?.takeIf { Properties.TYPE !in ignoreProperties }?.let { applyTo.type = it }

        // Modify meta
        val meta = applyTo.itemMeta ?: return applyTo
        customModelData?.takeIf { Properties.CUSTOM_MODEL_DATA !in ignoreProperties }
            ?.let { meta.setCustomModelData(it) }
        displayName?.takeIf { Properties.DISPLAY_NAME !in ignoreProperties }
            ?.let { meta.displayName(it.removeItalics()) }
        unbreakable?.takeIf { Properties.UNBREAKABLE !in ignoreProperties }
            ?.let { meta.isUnbreakable = it }
        lore?.takeIf { Properties.LORE !in ignoreProperties }
            ?.let { meta.lore(it.map { line -> line.removeItalics() }) }
        if (meta is Damageable && Properties.DAMAGE !in ignoreProperties) this@SerializableItemStack.damage?.let {
            meta.damage = it
        }
        if (itemFlags.isNotEmpty() && Properties.ITEM_FLAGS !in ignoreProperties) meta.addItemFlags(*itemFlags.toTypedArray())
        if (color != null && Properties.COLOR !in ignoreProperties) (meta as? PotionMeta)?.setColor(color)
            ?: (meta as? LeatherArmorMeta)?.setColor(color)
        if (potionData != null && Properties.POTION_DATA !in ignoreProperties) (meta as? PotionMeta)?.basePotionData =
            potionData
        if (enchantments != null && Properties.ENCHANTMENTS !in ignoreProperties) {
            enchantments.forEach { meta.addEnchant(it.enchant, it.level, true) }
        }
        if (knowledgeBookRecipes != null && Properties.KNOWLEDGE_BOOK_RECIPES !in ignoreProperties) {
            (meta as? KnowledgeBookMeta)?.recipes = knowledgeBookRecipes.map { it.getSubRecipeIDs() }.flatten()
        }
        if (attributeModifiers != null && Properties.ATTRIBUTE_MODIFIERS !in ignoreProperties) {
            val newAttributeModifiers = HashMultimap.create<Attribute?, AttributeModifier?>()
            attributeModifiers.forEach { newAttributeModifiers.put(it.attribute, it.modifier) }
            meta.attributeModifiers = newAttributeModifiers
        }
        applyTo.itemMeta = meta
        return applyTo
    }

    fun toItemStackOrNull(applyTo: ItemStack = ItemStack(type ?: Material.AIR)) =
        toItemStack().takeIf { it.type != Material.AIR }


    enum class Properties {
        TYPE,
        AMOUNT,
        CUSTOM_MODEL_DATA,
        DISPLAY_NAME,
        LORE,
        UNBREAKABLE,
        DAMAGE,
        PREFAB,
        ENCHANTMENTS,
        ITEM_FLAGS,
        ATTRIBUTE_MODIFIERS,
        POTION_DATA,
        KNOWLEDGE_BOOK_RECIPES,
        COLOR,
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        private val encodePrefab by lazy {
            Services.getViaClassNameOrNull<SerializablePrefabItemService, (ItemStack, String) -> Unit>()
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
    this.attributeModifiers?.forEach { a, m ->
        attributeList.add(
            SerializableAttribute(
                a,
                m
            )
        )
    }
    SerializableItemStack(
        type = type,
        amount = amount,
        customModelData = if (this.hasCustomModelData()) this.customModelData else null,
        displayName = if (this.hasDisplayName()) this.displayName() else null,
        unbreakable = this?.isUnbreakable,
        lore = if (this.hasLore()) this.lore() else null,
        damage = (this as? Damageable)?.damage,
        enchantments = enchants.map { SerializableEnchantment(it.key, it.value) },
        knowledgeBookRecipes = (this as? KnowledgeBookMeta)?.recipes?.map { it.getItemPrefabFromRecipe() }?.flatten()
            ?: emptyList(),
        itemFlags = this?.itemFlags?.toList() ?: listOf(),
        attributeModifiers = attributeList,
        potionData = (this as? PotionMeta)?.basePotionData,
        color = (this as? PotionMeta)?.color ?: (this as? LeatherArmorMeta)?.color
    ) //TODO perhaps this should encode prefab too?
}

private fun String.getSubRecipeIDs(): MutableList<NamespacedKey> {
    val recipes = mutableListOf<NamespacedKey>()
    Bukkit.recipeIterator().forEachRemaining { recipe ->
        if (recipe !is Keyed) return@forEachRemaining
        if (recipe.key.namespace == NamespacedKey.MINECRAFT_NAMESPACE) {
            recipes.add(recipe.key)
        } else if (recipe.key.asString().dropLast(1) == this) {
            recipes.add(recipe.key)
        }
    }
    return recipes
}

private fun NamespacedKey.getItemPrefabFromRecipe(): MutableList<String> {
    val recipes = mutableListOf<String>()
    Bukkit.recipeIterator().forEachRemaining { recipe ->
        if (recipe !is Keyed) return@forEachRemaining
        if (recipe.key.namespace == NamespacedKey.MINECRAFT_NAMESPACE) {
            recipes.add(recipe.key.asString())
        } else if (recipe.key == this) {
            recipes.add(recipe.key.asString().dropLast(1))
        }
    }
    return recipes
}


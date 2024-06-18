@file:UseSerializers(MiniMessageSerializer::class)

package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.di.DI
import com.mineinabyss.idofront.messaging.idofrontLogger
import com.mineinabyss.idofront.nms.hideAttributeTooltipWithItemFlagSet
import com.mineinabyss.idofront.plugin.Plugins
import com.mineinabyss.idofront.serialization.recipes.options.IngredientOption
import com.mineinabyss.idofront.textcomponents.miniMsg
import com.mineinabyss.idofront.textcomponents.serialize
import com.nexomc.nexo.NexoPlugin
import com.nexomc.nexo.api.NexoItems
import dev.lone.itemsadder.api.CustomStack
import io.lumine.mythiccrucible.MythicCrucible
import io.papermc.paper.component.DataComponentTypes
import io.papermc.paper.component.item.ItemLore
import kotlinx.serialization.Contextual
import kotlinx.serialization.*
import kotlinx.serialization.EncodeDefault.Mode.NEVER
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.*
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemRarity
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.KnowledgeBookMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.inventory.meta.components.JukeboxPlayableComponent
import org.bukkit.inventory.meta.components.ToolComponent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionType

typealias SerializableItemStack = @Serializable(with = SerializableItemStackSerializer::class) BaseSerializableItemStack

/**
 * A wrapper for [ItemStack] that uses [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization).
 * Allows for easy-to-use serialization to JSON (or YAML with kaml).
 *
 * Currently missing many things spigot's item serialization contains, but way cleaner to use!
 *
 * See [MiniMessage docs](https://docs.adventure.kyori.net/minimessage/format.html) for formatting info!
 */
@Serializable
data class BaseSerializableItemStack(
    @EncodeDefault(NEVER) val type: @Serializable(with = MaterialByNameSerializer::class) Material? = null,
    @EncodeDefault(NEVER) val amount: Int? = null,
    @EncodeDefault(NEVER) val customModelData: SerializableDataTypes.CustomModelData? = null,
    @EncodeDefault(NEVER) @SerialName("itemName") private val _itemName: String? = null,
    // This is private as we only want to use itemName in configs
    @EncodeDefault(NEVER) @SerialName("customName") private val _customName: String? = null,
    @EncodeDefault(NEVER) @SerialName("lore") private val _lore: List<String>? = null,
    @EncodeDefault(NEVER) val unbreakable: SerializableDataTypes.Unbreakable? = null,
    @EncodeDefault(NEVER) val damage: Int? = null,
    @EncodeDefault(NEVER) val maxDamage: Int? = null,
    @EncodeDefault(NEVER) val itemFlags: List<ItemFlag>? = null,
    @EncodeDefault(NEVER) val enchantments: SerializableDataTypes.Enchantments? = null,
    @EncodeDefault(NEVER) val potionType: SerializableDataTypes.PotionContents? = null,
    @EncodeDefault(NEVER) val attributeModifiers: SerializableDataTypes.AttributeModifiers? = null,
    @EncodeDefault(NEVER) val knowledgeBookRecipes: List<String>? = null,
    @EncodeDefault(NEVER) val dyedColor: SerializableDataTypes.DyedColor? = null,
    @EncodeDefault(NEVER) val food: SerializableDataTypes.FoodProperties? = null,
    @EncodeDefault(NEVER) val tool: SerializableDataTypes.Tool? = null,
    @EncodeDefault(NEVER) val jukeboxPlayable: @Serializable(with = JukeboxPlayableSerializer::class) JukeboxPlayableComponent? = null,
    @EncodeDefault(NEVER) val hideTooltip: Boolean? = null,
    @EncodeDefault(NEVER) val isFireResistant: Boolean? = null,
    @EncodeDefault(NEVER) val enchantmentGlintOverride: Boolean? = null,
    @EncodeDefault(NEVER) val maxStackSize: Int? = null,
    @EncodeDefault(NEVER) val rarity: ItemRarity? = null,

    @EncodeDefault(NEVER) val prefab: String? = null,
    @EncodeDefault(NEVER) val tag: String? = null,
    @EncodeDefault(NEVER) val recipeOptions: List<IngredientOption> = listOf(),

    // Unvalued DataTypes
    @EncodeDefault(NEVER) @Contextual val fireResistant: SerializableDataTypes.FireResistant? = null,
    @EncodeDefault(NEVER) @Contextual val hideTooltip: SerializableDataTypes.HideToolTip? = null,
    @EncodeDefault(NEVER) @Contextual val hideAdditionalTooltip: SerializableDataTypes.HideAdditionalTooltip? = null,
    @EncodeDefault(NEVER) @Contextual val creativeSlotLock: SerializableDataTypes.CreativeSlotLock? = null,
    @EncodeDefault(NEVER) @Contextual val intangibleProjectile: SerializableDataTypes.IntangibleProjectile? = null,

    // Third-party plugins
    @EncodeDefault(NEVER) val crucibleItem: String? = null,
    @EncodeDefault(NEVER) val nexoItem: String? = null,
    @EncodeDefault(NEVER) val itemsadderItem: String? = null,
) {
    private fun Component.removeItalics() =
        Component.text().decoration(TextDecoration.ITALIC, false).build().append(this)

    @Transient
    val itemName = _itemName?.miniMsg()
    @Transient
    val customName = _customName?.miniMsg()
    @Transient
    val lore = _lore?.map { it.miniMsg() }

    /**
     * Converts this serialized item's data to an [ItemStack], optionally applying the changes to an
     * [existing item][applyTo].
     */
    fun toItemStack(
        applyTo: ItemStack = ItemStack(type ?: Material.AIR),
    ): ItemStack {
        // Import ItemStack from Crucible
        crucibleItem?.let { id ->
            if (Plugins.isEnabled<MythicCrucible>()) {
                MythicCrucible.core().itemManager.getItemStack(id)?.let {
                    applyTo.type = it.type
                    applyTo.itemMeta = it.itemMeta
                } ?: idofrontLogger.w("No Crucible item found with id $id")
            } else {
                idofrontLogger.w("Tried to import Crucible item, but MythicCrucible was not enabled")
            }
        }

        // Import ItemStack from Nexo
        nexoItem?.let { id ->
            if (Plugins.isEnabled<NexoPlugin>()) {
                NexoItems.itemFromId(id)?.build()?.let {
                    applyTo.type = it.type
                    applyTo.itemMeta = it.itemMeta
                } ?: idofrontLogger.w("No Nexo item found with id $id")
            } else {
                idofrontLogger.w("Tried to import Nexo item, but Nexo was not enabled")
            }
        }

        // Import ItemStack from ItemsAdder
        itemsadderItem?.let { id ->
            if (Plugins.isEnabled("ItemsAdder")) {
                CustomStack.getInstance(id)?.itemStack?.let {
                    applyTo.type = it.type
                    applyTo.itemMeta = it.itemMeta
                } ?: idofrontLogger.w("No ItemsAdder item found with id $id")
            } else {
                idofrontLogger.w("Tried to import ItemsAdder item, but ItemsAdder was not enabled")
            }
        }

        // Support for our prefab system in geary.
        prefab?.let { encodePrefab.invoke(applyTo, it) } ?: applyTo

        // Modify item
        amount?.let { applyTo.amount = it }
        type?.let { applyTo.type = type }

        // Modify meta
        val meta = applyTo.itemMeta ?: return applyTo
        if (itemFlags?.isNotEmpty() == true) meta.addItemFlags(*itemFlags.toTypedArray())
        if (knowledgeBookRecipes != null) (meta as? KnowledgeBookMeta)?.recipes = knowledgeBookRecipes.map { it.getSubRecipeIDs() }.flatten()
        applyTo.itemMeta = meta

        SerializableDataTypes.setData(applyTo, DataComponentTypes.ITEM_NAME, itemName)
        SerializableDataTypes.setData(applyTo, DataComponentTypes.CUSTOM_NAME, customName)
        SerializableDataTypes.setData(applyTo, DataComponentTypes.LORE, lore?.let { ItemLore.lore(lore) })
        customModelData?.setDataType(applyTo)

        enchantments?.setDataType(applyTo)
        potionType?.setDataType(applyTo)
        attributeModifiers?.setDataType(applyTo)

        SerializableDataTypes.setData(applyTo, DataComponentTypes.DAMAGE, damage)
        SerializableDataTypes.setData(applyTo, DataComponentTypes.MAX_DAMAGE, maxDamage)
        SerializableDataTypes.setData(applyTo, DataComponentTypes.MAX_STACK_SIZE, maxStackSize)
        SerializableDataTypes.setData(applyTo, DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, enchantmentGlintOverride)
        unbreakable?.setDataType(applyTo)
        food?.setDataType(applyTo)
        tool?.setDataType(applyTo)

        fireResistant?.setDataType(applyTo)
        hideTooltip?.setDataType(applyTo)
        hideAdditionalTooltip?.setDataType(applyTo)
        creativeSlotLock?.setDataType(applyTo)
        intangibleProjectile?.setDataType(applyTo)

        return applyTo.hideAttributeTooltipWithItemFlagSet()
    }

    fun toItemStackOrNull(applyTo: ItemStack = ItemStack(type ?: Material.AIR)) =
        toItemStack().takeUnless { it.isEmpty }

    /** @return whether applying this [SerializableItemStack] to [item] would keep [item] identical. */
    fun matches(item: ItemStack): Boolean {
        return item == toItemStack(applyTo = item.clone())
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        private val encodePrefab by DI.observe<SerializablePrefabItemService>()
    }
}

/**
 * Converts an [ItemStack] to [SerializableItemStack]
 *
 * @see SerializableItemStack
 */
fun ItemStack.toSerializable(): SerializableItemStack = with(itemMeta) {
    val attributeList = mutableListOf<SerializableAttribute>()
    this.attributeModifiers?.forEach { a, m -> attributeList += SerializableAttribute(a, m) }
    SerializableItemStack(
        type = type,
        amount = amount.takeIf { it != 1 },
        itemName = getData(DataComponentTypes.ITEM_NAME),
        customName = getData(DataComponentTypes.CUSTOM_NAME),
        customModelData = getData(DataComponentTypes.CUSTOM_MODEL_DATA)?.let(SerializableDataTypes::CustomModelData),
        unbreakable = getData(DataComponentTypes.UNBREAKABLE)?.let(SerializableDataTypes::Unbreakable),
        lore = getData(DataComponentTypes.LORE)?.lines(),
        damage = getData(DataComponentTypes.DAMAGE),
        maxDamage = getData(DataComponentTypes.MAX_DAMAGE),
        maxStackSize = getData(DataComponentTypes.MAX_STACK_SIZE),
        rarity = getData(DataComponentTypes.RARITY),
        enchantmentGlintOverride = getData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE),

        enchantments = getData(DataComponentTypes.ENCHANTMENTS)?.let(SerializableDataTypes::Enchantments),
        attributeModifiers = getData(DataComponentTypes.ATTRIBUTE_MODIFIERS)?.let(SerializableDataTypes::AttributeModifiers),
        potionType = getData(DataComponentTypes.POTION_CONTENTS)?.let(SerializableDataTypes::PotionContents),
        dyedColor = getData(DataComponentTypes.DYED_COLOR)?.let(SerializableDataTypes::DyedColor),
        food = getData(DataComponentTypes.FOOD)?.let(SerializableDataTypes::FoodProperties),
        tool = getData(DataComponentTypes.TOOL)?.let(SerializableDataTypes::Tool),

        fireResistant = hasData(DataComponentTypes.FIRE_RESISTANT).takeIf { it }?.let { SerializableDataTypes.FireResistant(true) },
        hideTooltip = hasData(DataComponentTypes.HIDE_TOOLTIP).takeIf { it }?.let { SerializableDataTypes.HideToolTip(true) },
        hideAdditionalTooltip = hasData(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP).takeIf { it }?.let { SerializableDataTypes.HideAdditionalTooltip(true) },
        creativeSlotLock = hasData(DataComponentTypes.CREATIVE_SLOT_LOCK).takeIf { it }?.let { SerializableDataTypes.CreativeSlotLock(true) },
        intangibleProjectile = hasData(DataComponentTypes.INTANGIBLE_PROJECTILE).takeIf { it }?.let { SerializableDataTypes.IntangibleProjectile(true) },

        knowledgeBookRecipes = ((this as? KnowledgeBookMeta)?.recipes?.map { it.getItemPrefabFromRecipe() }?.flatten()
            ?: emptyList()).takeIf { it.isNotEmpty() },
        itemFlags = (this?.itemFlags?.toList() ?: listOf()).takeIf { it.isNotEmpty() },

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
        when {
            recipe !is Keyed -> return@forEachRemaining
            recipe.key.namespace == NamespacedKey.MINECRAFT_NAMESPACE -> recipes += recipe.key.asString()
            recipe.key == this -> recipes += recipe.key.asString().dropLast(1)
        }
    }
    return recipes
}


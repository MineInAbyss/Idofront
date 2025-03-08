@file:UseSerializers(MiniMessageSerializer::class)

package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.di.DI
import com.mineinabyss.idofront.messaging.idofrontLogger
import com.mineinabyss.idofront.plugin.Plugins
import com.mineinabyss.idofront.serialization.recipes.options.IngredientOption
import com.mineinabyss.idofront.textcomponents.miniMsg
import com.mineinabyss.idofront.textcomponents.serialize
import com.nexomc.nexo.NexoPlugin
import com.nexomc.nexo.api.NexoItems
import dev.lone.itemsadder.api.CustomStack
import io.lumine.mythiccrucible.MythicCrucible
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.ItemLore
import io.papermc.paper.datacomponent.item.MapDecorations
import io.papermc.paper.item.MapPostProcessing
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import kotlinx.serialization.Contextual
import kotlinx.serialization.*
import kotlinx.serialization.EncodeDefault.Mode.NEVER
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Keyed
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.inventory.ItemRarity
import org.bukkit.inventory.ItemStack

typealias SerializableItemStack = @Serializable(with = SerializableItemStackSerializer::class) BaseSerializableItemStack

/**
 * A wrapper for [ItemStack] that uses [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization).
 * Allows for easy-to-use serialization to JSON (or YAML with kaml).
 */
@Serializable
data class BaseSerializableItemStack(
    @EncodeDefault(NEVER) val type: @Serializable(with = MaterialByNameSerializer::class) Material? = null,
    @EncodeDefault(NEVER) val amount: Int? = null,
    @EncodeDefault(NEVER) val customModelData: SerializableDataTypes.CustomModelData? = null,
    @EncodeDefault(NEVER) val itemModel: @Serializable(KeySerializer::class) Key? = null,
    @EncodeDefault(NEVER) val tooltipStyle: @Serializable(KeySerializer::class) Key? = null,
    @EncodeDefault(NEVER) val instrument: @Serializable(KeySerializer::class) Key? = null,

    @EncodeDefault(NEVER) @SerialName("itemName") private val _itemName: String? = null,
    // This is private as we only want to use itemName in configs
    @EncodeDefault(NEVER) @SerialName("customName") private val _customName: String? = null,
    @EncodeDefault(NEVER) @SerialName("lore") private val _lore: List<String>? = null,

    @EncodeDefault(NEVER) val unbreakable: SerializableDataTypes.Unbreakable? = null,
    @EncodeDefault(NEVER) val damage: Int? = null,
    @EncodeDefault(NEVER) val maxDamage: Int? = null,
    @EncodeDefault(NEVER) val enchantments: SerializableDataTypes.Enchantments? = null,
    @EncodeDefault(NEVER) val storedEnchantments: SerializableDataTypes.StoredEnchantments? = null,
    @EncodeDefault(NEVER) val potionContents: SerializableDataTypes.PotionContents? = null,
    @EncodeDefault(NEVER) val attributeModifiers: SerializableDataTypes.AttributeModifiers? = null,
    @EncodeDefault(NEVER) val useCooldown: SerializableDataTypes.UseCooldown? = null,
    @EncodeDefault(NEVER) val useRemainder: SerializableDataTypes.UseRemainder? = null,
    @EncodeDefault(NEVER) val consumable: SerializableDataTypes.Consumable? = null,
    @EncodeDefault(NEVER) val food: SerializableDataTypes.Food? = null,
    @EncodeDefault(NEVER) val tool: SerializableDataTypes.Tool? = null,
    @EncodeDefault(NEVER) val enchantable: SerializableDataTypes.Enchantable? = null,
    @EncodeDefault(NEVER) val repairable: SerializableDataTypes.Repairable? = null,
    @EncodeDefault(NEVER) val canPlaceOn: SerializableDataTypes.CanPlaceOn? = null,
    @EncodeDefault(NEVER) val canBreak: SerializableDataTypes.CanBreak? = null,
    @EncodeDefault(NEVER) val dyedColor: SerializableDataTypes.DyedColor? = null,
    @EncodeDefault(NEVER) val mapColor: SerializableDataTypes.MapColor? = null,
    @EncodeDefault(NEVER) val mapDecorations: List<SerializableDataTypes.MapDecoration>? = null,
    @EncodeDefault(NEVER) val equippable: SerializableDataTypes.Equippable? = null,
    @EncodeDefault(NEVER) val trim: SerializableDataTypes.Trim? = null,
    @EncodeDefault(NEVER) val jukeboxPlayable: SerializableDataTypes.JukeboxPlayable? = null,
    @EncodeDefault(NEVER) val chargedProjectiles: SerializableDataTypes.ChargedProjectiles? = null,
    @EncodeDefault(NEVER) val bundleContents: SerializableDataTypes.BundleContent? = null,
    @EncodeDefault(NEVER) val writableBook: SerializableDataTypes.WritableBook? = null,
    @EncodeDefault(NEVER) val writtenBook: SerializableDataTypes.WrittenBook? = null,
    @EncodeDefault(NEVER) val damageResistant: SerializableDataTypes.DamageResistant? = null,
    @EncodeDefault(NEVER) val deathProtection: SerializableDataTypes.DeathProtection? = null,

    @EncodeDefault(NEVER) val recipes: List<@Serializable(KeySerializer::class) Key>? = null,
    @EncodeDefault(NEVER) val enchantmentGlintOverride: Boolean? = null,
    @EncodeDefault(NEVER) val maxStackSize: Int? = null,
    @EncodeDefault(NEVER) val rarity: ItemRarity? = null,
    @EncodeDefault(NEVER) val repairCost: Int? = null,
    @EncodeDefault(NEVER) val mapId: SerializableDataTypes.MapId? = null,
    @EncodeDefault(NEVER) val mapPostProcessing: MapPostProcessing? = null,

    // Block-specific DataTypes
    //@EncodeDefault(NEVER) val lock: String? = null,
    @EncodeDefault(NEVER) val noteBlockSound: @Serializable(KeySerializer::class) Key? = null,
    @EncodeDefault(NEVER) val potDecorations: SerializableDataTypes.PotDecorations? = null,


    @EncodeDefault(NEVER) val prefab: String? = null,
    @EncodeDefault(NEVER) val tag: String? = null,
    @EncodeDefault(NEVER) val recipeOptions: List<IngredientOption> = listOf(),

    // Unvalued DataTypes
    @EncodeDefault(NEVER) @Contextual val hideTooltip: SerializableDataTypes.HideToolTip? = null,
    @EncodeDefault(NEVER) @Contextual val hideAdditionalTooltip: SerializableDataTypes.HideAdditionalTooltip? = null,
    @EncodeDefault(NEVER) @Contextual val intangibleProjectile: SerializableDataTypes.IntangibleProjectile? = null,
    @EncodeDefault(NEVER) @Contextual val glider: SerializableDataTypes.Glider? = null,

    // Third-party plugins
    @EncodeDefault(NEVER) val crucibleItem: String? = null,
    @EncodeDefault(NEVER) val nexoItem: String? = null,
    @EncodeDefault(NEVER) val itemsadderItem: String? = null,
) {
    private fun Component.removeItalics() =
        Component.text().decoration(TextDecoration.ITALIC, false).build().append(this)

    @Transient val itemName = _itemName?.miniMsg()
    @Transient val customName = _customName?.miniMsg()
    @Transient val lore = _lore?.map { it.miniMsg() }

    /**
     * Converts this serialized item's data to an [ItemStack], optionally applying the changes to an
     * [existing item][applyTo].
     */
    fun toItemStack(applyTo: ItemStack = ItemStack.of(type ?: Material.AIR)): ItemStack {
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

        SerializableDataTypes.setData(applyTo, DataComponentTypes.ITEM_NAME, itemName)
        SerializableDataTypes.setData(applyTo, DataComponentTypes.CUSTOM_NAME, customName)
        SerializableDataTypes.setData(applyTo, DataComponentTypes.LORE, lore?.let(ItemLore::lore))
        SerializableDataTypes.setData(applyTo, DataComponentTypes.ITEM_MODEL, itemModel)
        SerializableDataTypes.setData(applyTo, DataComponentTypes.TOOLTIP_STYLE, tooltipStyle)
        instrument?.let(Registry.INSTRUMENT::get)?.also { SerializableDataTypes.setData(applyTo, DataComponentTypes.INSTRUMENT, it) }

        enchantments?.setDataType(applyTo)
        storedEnchantments?.setDataType(applyTo)
        potionContents?.setDataType(applyTo)
        attributeModifiers?.setDataType(applyTo)
        customModelData?.setDataType(applyTo)

        SerializableDataTypes.setData(applyTo, DataComponentTypes.REPAIR_COST, repairCost)
        SerializableDataTypes.setData(applyTo, DataComponentTypes.DAMAGE, damage)
        SerializableDataTypes.setData(applyTo, DataComponentTypes.MAX_DAMAGE, maxDamage)
        SerializableDataTypes.setData(applyTo, DataComponentTypes.MAX_STACK_SIZE, maxStackSize)
        SerializableDataTypes.setData(applyTo, DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, enchantmentGlintOverride)
        SerializableDataTypes.setData(applyTo, DataComponentTypes.RECIPES, recipes)

        unbreakable?.setDataType(applyTo)
        useCooldown?.setDataType(applyTo)
        useRemainder?.setDataType(applyTo)
        consumable?.setDataType(applyTo)
        food?.setDataType(applyTo)
        tool?.setDataType(applyTo)
        enchantable?.setDataType(applyTo)
        repairable?.setDataType(applyTo)
        canPlaceOn?.setDataType(applyTo)
        canBreak?.setDataType(applyTo)
        equippable?.setDataType(applyTo)
        trim?.setDataType(applyTo)
        jukeboxPlayable?.setDataType(applyTo)
        chargedProjectiles?.setDataType(applyTo)
        bundleContents?.setDataType(applyTo)
        writableBook?.setDataType(applyTo)
        writtenBook?.setDataType(applyTo)
        damageResistant?.setDataType(applyTo)
        mapColor?.setDataType(applyTo)
        mapId?.setDataType(applyTo)

        mapPostProcessing?.let { applyTo.setData(DataComponentTypes.MAP_POST_PROCESSING, it) }
        mapDecorations?.let {
            applyTo.setData(
                DataComponentTypes.MAP_DECORATIONS,
                MapDecorations.mapDecorations(SerializableDataTypes.MapDecoration.toPaperDecorations(mapDecorations))
            )
        }

        //SerializableDataTypes.setData(applyTo, DataComponentTypes.LOCK, LockCode)
        SerializableDataTypes.setData(applyTo, DataComponentTypes.NOTE_BLOCK_SOUND, noteBlockSound)
        potDecorations?.setDataType(applyTo)

        SerializableDataTypes.setData(applyTo, DataComponentTypes.HIDE_TOOLTIP, hideTooltip)
        SerializableDataTypes.setData(applyTo, DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP, hideAdditionalTooltip)
        SerializableDataTypes.setData(applyTo, DataComponentTypes.INTANGIBLE_PROJECTILE, intangibleProjectile)

        return applyTo
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
        _itemName = getData(DataComponentTypes.ITEM_NAME)?.serialize(),
        _customName = getData(DataComponentTypes.CUSTOM_NAME)?.serialize(),
        _lore = getData(DataComponentTypes.LORE)?.lines()?.map { it.serialize() },

        itemModel = getData(DataComponentTypes.ITEM_MODEL),
        tooltipStyle = getData(DataComponentTypes.TOOLTIP_STYLE),
        damage = getData(DataComponentTypes.DAMAGE),
        maxDamage = getData(DataComponentTypes.MAX_DAMAGE),
        maxStackSize = getData(DataComponentTypes.MAX_STACK_SIZE),
        rarity = getData(DataComponentTypes.RARITY),
        enchantmentGlintOverride = getData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE),
        repairCost = getData(DataComponentTypes.REPAIR_COST),
        recipes = getData(DataComponentTypes.RECIPES),
        instrument = getData(DataComponentTypes.INSTRUMENT)?.let {
            RegistryAccess.registryAccess().getRegistry(RegistryKey.INSTRUMENT).getKey(it)
        },

        customModelData = getData(DataComponentTypes.CUSTOM_MODEL_DATA)?.let(SerializableDataTypes::CustomModelData),
        unbreakable = getData(DataComponentTypes.UNBREAKABLE)?.let(SerializableDataTypes::Unbreakable),
        enchantments = getData(DataComponentTypes.ENCHANTMENTS)?.let(SerializableDataTypes::Enchantments),
        storedEnchantments = getData(DataComponentTypes.STORED_ENCHANTMENTS)?.let(SerializableDataTypes::StoredEnchantments),
        attributeModifiers = getData(DataComponentTypes.ATTRIBUTE_MODIFIERS)?.let(SerializableDataTypes::AttributeModifiers),
        potionContents = getData(DataComponentTypes.POTION_CONTENTS)?.let(SerializableDataTypes::PotionContents),
        dyedColor = getData(DataComponentTypes.DYED_COLOR)?.let(SerializableDataTypes::DyedColor),
        useCooldown = getData(DataComponentTypes.USE_COOLDOWN)?.let(SerializableDataTypes::UseCooldown),
        useRemainder = getData(DataComponentTypes.USE_REMAINDER)?.let(SerializableDataTypes::UseRemainder),
        consumable = getData(DataComponentTypes.CONSUMABLE)?.let(SerializableDataTypes::Consumable),
        food = getData(DataComponentTypes.FOOD)?.let(SerializableDataTypes::Food),
        tool = getData(DataComponentTypes.TOOL)?.let(SerializableDataTypes::Tool),
        enchantable = getData(DataComponentTypes.ENCHANTABLE)?.let(SerializableDataTypes::Enchantable),
        repairable = getData(DataComponentTypes.REPAIRABLE)?.let(SerializableDataTypes::Repairable),
        canPlaceOn = getData(DataComponentTypes.CAN_PLACE_ON)?.let(SerializableDataTypes::CanPlaceOn),
        canBreak = getData(DataComponentTypes.CAN_BREAK)?.let(SerializableDataTypes::CanBreak),
        equippable = getData(DataComponentTypes.EQUIPPABLE)?.let(SerializableDataTypes::Equippable),
        trim = getData(DataComponentTypes.TRIM)?.let(SerializableDataTypes::Trim),
        jukeboxPlayable = getData(DataComponentTypes.JUKEBOX_PLAYABLE)?.let(SerializableDataTypes::JukeboxPlayable),
        chargedProjectiles = getData(DataComponentTypes.CHARGED_PROJECTILES)?.let(SerializableDataTypes::ChargedProjectiles),
        bundleContents = getData(DataComponentTypes.BUNDLE_CONTENTS)?.let(SerializableDataTypes::BundleContent),
        writableBook = getData(DataComponentTypes.WRITABLE_BOOK_CONTENT)?.let(SerializableDataTypes::WritableBook),
        writtenBook = getData(DataComponentTypes.WRITTEN_BOOK_CONTENT)?.let(SerializableDataTypes::WrittenBook),
        damageResistant = getData(DataComponentTypes.DAMAGE_RESISTANT)?.let(SerializableDataTypes::DamageResistant),
        deathProtection = getData(DataComponentTypes.DEATH_PROTECTION)?.let(SerializableDataTypes::DeathProtection),
        mapColor = getData(DataComponentTypes.MAP_COLOR)?.let(SerializableDataTypes::MapColor),
        mapId = getData(DataComponentTypes.MAP_ID)?.let(SerializableDataTypes::MapId),

        mapPostProcessing = getData(DataComponentTypes.MAP_POST_PROCESSING),
        mapDecorations = getData(DataComponentTypes.MAP_DECORATIONS)?.decorations()?.values?.map(SerializableDataTypes::MapDecoration),

        noteBlockSound = getData(DataComponentTypes.NOTE_BLOCK_SOUND),
        potDecorations = getData(DataComponentTypes.POT_DECORATIONS)?.let(SerializableDataTypes::PotDecorations),

        hideTooltip = SerializableDataTypes.HideToolTip.takeIf { hasData(DataComponentTypes.HIDE_TOOLTIP) },
        hideAdditionalTooltip = SerializableDataTypes.HideAdditionalTooltip.takeIf { hasData(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP) },
        intangibleProjectile = SerializableDataTypes.IntangibleProjectile.takeIf { hasData(DataComponentTypes.INTANGIBLE_PROJECTILE) },

    )
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


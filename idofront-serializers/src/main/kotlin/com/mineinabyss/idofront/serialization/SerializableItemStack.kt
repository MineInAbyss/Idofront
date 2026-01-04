@file:UseSerializers(MiniMessageSerializer::class)

package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.di.DI
import com.mineinabyss.idofront.messaging.idofrontLogger
import com.mineinabyss.idofront.plugin.Plugins
import com.mineinabyss.idofront.serialization.recipes.options.IngredientOption
import com.mineinabyss.idofront.textcomponents.miniMsg
import com.mineinabyss.idofront.textcomponents.serialize
import com.mineinabyss.idofront.util.ifNotEmpty
import com.nexomc.nexo.api.NexoItems
import com.nexomc.protectionlib.ProtectionLib.canBreak
import io.lumine.mythic.bukkit.utils.lib.jooq.impl.DSL.type
import io.papermc.paper.datacomponent.DataComponentType
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.BlocksAttacks.blocksAttacks
import io.papermc.paper.datacomponent.item.BundleContents.bundleContents
import io.papermc.paper.datacomponent.item.ChargedProjectiles.chargedProjectiles
import io.papermc.paper.datacomponent.item.Consumable.consumable
import io.papermc.paper.datacomponent.item.DamageResistant.damageResistant
import io.papermc.paper.datacomponent.item.Enchantable.enchantable
import io.papermc.paper.datacomponent.item.Equippable.equippable
import io.papermc.paper.datacomponent.item.FoodProperties.food
import io.papermc.paper.datacomponent.item.ItemLore
import io.papermc.paper.datacomponent.item.JukeboxPlayable.jukeboxPlayable
import io.papermc.paper.datacomponent.item.KineticWeapon.kineticWeapon
import io.papermc.paper.datacomponent.item.MapDecorations
import io.papermc.paper.datacomponent.item.MapDecorations.mapDecorations
import io.papermc.paper.datacomponent.item.MapId.mapId
import io.papermc.paper.datacomponent.item.PiercingWeapon.piercingWeapon
import io.papermc.paper.datacomponent.item.PotionContents.potionContents
import io.papermc.paper.datacomponent.item.Repairable.repairable
import io.papermc.paper.datacomponent.item.SwingAnimation
import io.papermc.paper.datacomponent.item.SwingAnimation.swingAnimation
import io.papermc.paper.datacomponent.item.Tool.tool
import io.papermc.paper.datacomponent.item.TooltipDisplay.tooltipDisplay
import io.papermc.paper.datacomponent.item.UseCooldown.useCooldown
import io.papermc.paper.datacomponent.item.UseRemainder.useRemainder
import io.papermc.paper.datacomponent.item.Weapon.weapon
import io.papermc.paper.item.MapPostProcessing
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import kotlinx.serialization.Contextual
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.EncodeDefault.Mode.NEVER
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Keyed
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.damage.DamageType
import org.bukkit.inventory.ItemRarity
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.RecipeChoice
import team.unnamed.creative.item.tint.TintSource.mapColor

typealias SerializableItemStack = @Serializable(with = SerializableItemStackSerializer::class) BaseSerializableItemStack

/**
 * A wrapper for [ItemStack] that uses [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization).
 * Allows for easy-to-use serialization to JSON (or YAML with kaml).
 */
@Suppress("UnstableApiUsage")
@Serializable
data class BaseSerializableItemStack(
    @EncodeDefault(NEVER) val type: @Serializable(with = MaterialByNameSerializer::class) Material? = null,
    @EncodeDefault(NEVER) val amount: Int? = null,
    @EncodeDefault(NEVER) val customModelData: SerializableDataTypes.CustomModelData? = null,
    @EncodeDefault(NEVER) val itemModel: @Serializable(KeySerializer::class) Key? = null,
    @EncodeDefault(NEVER) val tooltipStyle: @Serializable(KeySerializer::class) Key? = null,
    @EncodeDefault(NEVER) val instrument: @Serializable(KeySerializer::class) Key? = null,
    @EncodeDefault(NEVER) val breakSound: @Serializable(KeySerializer::class) Key? = null,

    @EncodeDefault(NEVER) @SerialName("itemName") private val _itemName: String? = null,
    // This is private as we only want to use itemName in configs
    @EncodeDefault(NEVER) @SerialName("customName") private val _customName: String? = null,
    @EncodeDefault(NEVER) @SerialName("lore") private val _lore: List<String>? = null,

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
    @EncodeDefault(NEVER) val weapon: SerializableDataTypes.Weapon? = null,
    @EncodeDefault(NEVER) val blocksAttacks: SerializableDataTypes.BlocksAttacks? = null,
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
    @EncodeDefault(NEVER) val tooltipDisplay: SerializableDataTypes.TooltipDisplay? = null,
    @EncodeDefault(NEVER) val paintingVariant: SerializableDataTypes.PaintingVariant? = null,

    @EncodeDefault(NEVER) val recipes: List<@Serializable(KeySerializer::class) Key>? = null,
    @EncodeDefault(NEVER) val enchantmentGlintOverride: Boolean? = null,
    @EncodeDefault(NEVER) val maxStackSize: Int? = null,
    @EncodeDefault(NEVER) val rarity: ItemRarity? = null,
    @EncodeDefault(NEVER) val repairCost: Int? = null,
    @EncodeDefault(NEVER) val potionDurationScale: Float? = null,
    @EncodeDefault(NEVER) val mapId: SerializableDataTypes.MapId? = null,
    @EncodeDefault(NEVER) val mapPostProcessing: MapPostProcessing? = null,

    @EncodeDefault(NEVER) val profile: SerializableDataTypes.Profile? = null,
    @EncodeDefault(NEVER) val damageType: DamageType? = null,
    @EncodeDefault(NEVER) val kineticWeapon: SerializableDataTypes.KineticWeapon? = null,
    @EncodeDefault(NEVER) val piercingWeapon: SerializableDataTypes.PiercingWeapon? = null,
    @EncodeDefault(NEVER) val swingAnimation: SerializableDataTypes.SwingAnimation? = null,
    @EncodeDefault(NEVER) val useEffects: SerializableDataTypes.UseEffects? = null,
    @EncodeDefault(NEVER) val minimumAttackCharge: Float? = null,

    // Block-specific DataTypes
    //@EncodeDefault(NEVER) val lock: String? = null,
    @EncodeDefault(NEVER) val noteBlockSound: @Serializable(KeySerializer::class) Key? = null,
    @EncodeDefault(NEVER) val potDecorations: SerializableDataTypes.PotDecorations? = null,


    @EncodeDefault(NEVER) val prefab: String? = null,
    @EncodeDefault(NEVER) val tag: @Serializable(NamespacedKeySerializer::class) NamespacedKey? = null,
    @EncodeDefault(NEVER) val recipeOptions: List<IngredientOption> = listOf(),

    // Unvalued DataTypes
    @EncodeDefault(NEVER) @Contextual val intangibleProjectile: SerializableDataTypes.IntangibleProjectile? = null,
    @EncodeDefault(NEVER) @Contextual val glider: SerializableDataTypes.Glider? = null,
    @EncodeDefault(NEVER) val unbreakable: SerializableDataTypes.Unbreakable? = null,

    // Third-party plugins
    @EncodeDefault(NEVER) val nexoItem: String = "",
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

        // Import ItemStack from Nexo
        nexoItem.ifNotEmpty { id ->
            if (Plugins.isEnabled("Nexo")) {
                NexoItems.itemFromId(id)?.build()?.let {
                    applyTo.type = it.type
                    applyTo.copyDataFrom(it) { true }
                } ?: idofrontLogger.w("No Nexo item found with id $id")
            } else {
                idofrontLogger.w("Tried to import Nexo item, but Nexo was not enabled")
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
        breakSound?.also { SerializableDataTypes.setData(applyTo, DataComponentTypes.BREAK_SOUND, it) }
        instrument?.let(RegistryAccess.registryAccess().getRegistry(RegistryKey.INSTRUMENT)::get)?.also {
            SerializableDataTypes.setData(applyTo, DataComponentTypes.INSTRUMENT, it)
        }

        paintingVariant?.setDataType(applyTo)
        enchantments?.setDataType(applyTo)
        storedEnchantments?.setDataType(applyTo)
        potionContents?.setDataType(applyTo)
        attributeModifiers?.setDataType(applyTo)
        customModelData?.setDataType(applyTo)
        tooltipDisplay?.setDataType(applyTo)
        paintingVariant?.setDataType(applyTo)

        SerializableDataTypes.setData(applyTo, DataComponentTypes.REPAIR_COST, repairCost)
        SerializableDataTypes.setData(applyTo, DataComponentTypes.POTION_DURATION_SCALE, potionDurationScale)
        SerializableDataTypes.setData(applyTo, DataComponentTypes.DAMAGE, damage)
        SerializableDataTypes.setData(applyTo, DataComponentTypes.MAX_DAMAGE, maxDamage)
        SerializableDataTypes.setData(applyTo, DataComponentTypes.MAX_STACK_SIZE, maxStackSize)
        SerializableDataTypes.setData(applyTo, DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, enchantmentGlintOverride)
        SerializableDataTypes.setData(applyTo, DataComponentTypes.RECIPES, recipes)

        useCooldown?.setDataType(applyTo)
        useRemainder?.setDataType(applyTo)
        consumable?.setDataType(applyTo)
        food?.setDataType(applyTo)
        tool?.setDataType(applyTo)
        weapon?.setDataType(applyTo)
        blocksAttacks?.setDataType(applyTo)
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

        profile?.setDataType(applyTo)
        kineticWeapon?.setDataType(applyTo)
        piercingWeapon?.setDataType(applyTo)
        swingAnimation?.setDataType(applyTo)
        useEffects?.setDataType(applyTo)
        SerializableDataTypes.setData(applyTo, DataComponentTypes.MINIMUM_ATTACK_CHARGE, minimumAttackCharge)


        mapPostProcessing?.let { applyTo.setData(DataComponentTypes.MAP_POST_PROCESSING, it) }
        mapDecorations?.let {
            applyTo.setData(
                DataComponentTypes.MAP_DECORATIONS,
                mapDecorations(SerializableDataTypes.MapDecoration.toPaperDecorations(mapDecorations))
            )
        }

        //SerializableDataTypes.setData(applyTo, DataComponentTypes.LOCK, LockCode)
        SerializableDataTypes.setData(applyTo, DataComponentTypes.NOTE_BLOCK_SOUND, noteBlockSound)
        potDecorations?.setDataType(applyTo)

        SerializableDataTypes.setData(applyTo, DataComponentTypes.INTANGIBLE_PROJECTILE, intangibleProjectile)
        SerializableDataTypes.setData(applyTo, DataComponentTypes.GLIDER, glider)
        SerializableDataTypes.setData(applyTo, DataComponentTypes.UNBREAKABLE, unbreakable)

        return applyTo
    }

    fun toItemStackOrNull(applyTo: ItemStack = ItemStack(type ?: Material.AIR)) =
        toItemStack(applyTo).takeUnless { it.isEmpty }

    fun toRecipeChoice(): RecipeChoice = toItemStackOrNull()?.let(RecipeChoice::ExactChoice) ?: RecipeChoice.empty()

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
    SerializableItemStack(
        type = type,
        amount = amount.takeIf { it != 1 },
        _itemName = dataIfOverriden(DataComponentTypes.ITEM_NAME)?.serialize(),
        _customName = dataIfOverriden(DataComponentTypes.CUSTOM_NAME)?.serialize(),
        _lore = dataIfOverriden(DataComponentTypes.LORE)?.lines()?.map { it.serialize() }.takeUnless { it.isNullOrEmpty() },

        itemModel = dataIfOverriden(DataComponentTypes.ITEM_MODEL),
        tooltipStyle = dataIfOverriden(DataComponentTypes.TOOLTIP_STYLE),
        damage = dataIfOverriden(DataComponentTypes.DAMAGE),
        maxDamage = dataIfOverriden(DataComponentTypes.MAX_DAMAGE),
        maxStackSize = dataIfOverriden(DataComponentTypes.MAX_STACK_SIZE),
        rarity = dataIfOverriden(DataComponentTypes.RARITY),
        enchantmentGlintOverride = dataIfOverriden(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE),
        repairCost = dataIfOverriden(DataComponentTypes.REPAIR_COST),
        potionDurationScale = dataIfOverriden(DataComponentTypes.POTION_DURATION_SCALE),
        recipes = dataIfOverriden(DataComponentTypes.RECIPES),
        instrument = dataIfOverriden(DataComponentTypes.INSTRUMENT)?.let {
            RegistryAccess.registryAccess().getRegistry(RegistryKey.INSTRUMENT).getKey(it)
        },

        customModelData = dataIfOverriden(DataComponentTypes.CUSTOM_MODEL_DATA)?.let(SerializableDataTypes::CustomModelData),
        enchantments = dataIfOverriden(DataComponentTypes.ENCHANTMENTS)?.let(SerializableDataTypes::Enchantments),
        storedEnchantments = dataIfOverriden(DataComponentTypes.STORED_ENCHANTMENTS)?.let(SerializableDataTypes::StoredEnchantments),
        attributeModifiers = dataIfOverriden(DataComponentTypes.ATTRIBUTE_MODIFIERS)?.let(SerializableDataTypes::AttributeModifiers),
        potionContents = dataIfOverriden(DataComponentTypes.POTION_CONTENTS)?.let(SerializableDataTypes::PotionContents),
        dyedColor = dataIfOverriden(DataComponentTypes.DYED_COLOR)?.let(SerializableDataTypes::DyedColor),
        useCooldown = dataIfOverriden(DataComponentTypes.USE_COOLDOWN)?.let(SerializableDataTypes::UseCooldown),
        useRemainder = dataIfOverriden(DataComponentTypes.USE_REMAINDER)?.let(SerializableDataTypes::UseRemainder),
        consumable = dataIfOverriden(DataComponentTypes.CONSUMABLE)?.let(SerializableDataTypes::Consumable),
        food = dataIfOverriden(DataComponentTypes.FOOD)?.let(SerializableDataTypes::Food),
        tool = dataIfOverriden(DataComponentTypes.TOOL)?.let(SerializableDataTypes::Tool),
        weapon = dataIfOverriden(DataComponentTypes.WEAPON)?.let(SerializableDataTypes::Weapon),
        blocksAttacks = dataIfOverriden(DataComponentTypes.BLOCKS_ATTACKS)?.let(SerializableDataTypes::BlocksAttacks),
        enchantable = dataIfOverriden(DataComponentTypes.ENCHANTABLE)?.let(SerializableDataTypes::Enchantable),
        repairable = dataIfOverriden(DataComponentTypes.REPAIRABLE)?.let(SerializableDataTypes::Repairable),
        canPlaceOn = dataIfOverriden(DataComponentTypes.CAN_PLACE_ON)?.let(SerializableDataTypes::CanPlaceOn),
        canBreak = dataIfOverriden(DataComponentTypes.CAN_BREAK)?.let(SerializableDataTypes::CanBreak),
        equippable = dataIfOverriden(DataComponentTypes.EQUIPPABLE)?.let(SerializableDataTypes::Equippable),
        trim = dataIfOverriden(DataComponentTypes.TRIM)?.let(SerializableDataTypes::Trim),
        jukeboxPlayable = dataIfOverriden(DataComponentTypes.JUKEBOX_PLAYABLE)?.let(SerializableDataTypes::JukeboxPlayable),
        chargedProjectiles = dataIfOverriden(DataComponentTypes.CHARGED_PROJECTILES)?.let(SerializableDataTypes::ChargedProjectiles),
        bundleContents = dataIfOverriden(DataComponentTypes.BUNDLE_CONTENTS)?.let(SerializableDataTypes::BundleContent),
        writableBook = dataIfOverriden(DataComponentTypes.WRITABLE_BOOK_CONTENT)?.let(SerializableDataTypes::WritableBook),
        writtenBook = dataIfOverriden(DataComponentTypes.WRITTEN_BOOK_CONTENT)?.let(SerializableDataTypes::WrittenBook),
        damageResistant = dataIfOverriden(DataComponentTypes.DAMAGE_RESISTANT)?.let(SerializableDataTypes::DamageResistant),
        deathProtection = dataIfOverriden(DataComponentTypes.DEATH_PROTECTION)?.let(SerializableDataTypes::DeathProtection),
        mapColor = dataIfOverriden(DataComponentTypes.MAP_COLOR)?.let(SerializableDataTypes::MapColor),
        mapId = dataIfOverriden(DataComponentTypes.MAP_ID)?.let(SerializableDataTypes::MapId),
        tooltipDisplay = dataIfOverriden(DataComponentTypes.TOOLTIP_DISPLAY)?.let(SerializableDataTypes::TooltipDisplay),
        paintingVariant = dataIfOverriden(DataComponentTypes.PAINTING_VARIANT)?.let(SerializableDataTypes::PaintingVariant),

        profile = dataIfOverriden(DataComponentTypes.PROFILE)?.let(SerializableDataTypes::Profile),
        kineticWeapon = dataIfOverriden(DataComponentTypes.KINETIC_WEAPON)?.let(SerializableDataTypes::KineticWeapon),
        piercingWeapon = dataIfOverriden(DataComponentTypes.PIERCING_WEAPON)?.let(SerializableDataTypes::PiercingWeapon),
        swingAnimation = dataIfOverriden(DataComponentTypes.SWING_ANIMATION)?.let(SerializableDataTypes::SwingAnimation),
        useEffects = dataIfOverriden(DataComponentTypes.USE_EFFECTS)?.let(SerializableDataTypes::UseEffects),
        minimumAttackCharge = dataIfOverriden(DataComponentTypes.MINIMUM_ATTACK_CHARGE),

        mapPostProcessing = dataIfOverriden(DataComponentTypes.MAP_POST_PROCESSING),
        mapDecorations = dataIfOverriden(DataComponentTypes.MAP_DECORATIONS)?.decorations()?.values?.map(SerializableDataTypes::MapDecoration),

        noteBlockSound = dataIfOverriden(DataComponentTypes.NOTE_BLOCK_SOUND),
        potDecorations = dataIfOverriden(DataComponentTypes.POT_DECORATIONS)?.let(SerializableDataTypes::PotDecorations),

        intangibleProjectile = SerializableDataTypes.IntangibleProjectile.takeIf { hasData(DataComponentTypes.INTANGIBLE_PROJECTILE) },
        glider = SerializableDataTypes.Glider.takeIf { hasData(DataComponentTypes.GLIDER) },
        unbreakable = SerializableDataTypes.Unbreakable.takeIf { hasData(DataComponentTypes.UNBREAKABLE) },

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

private fun <T : Any> ItemStack.dataIfOverriden(dataType: DataComponentType.Valued<T>): T? {
    return if (isDataOverridden(dataType)) return getData(dataType)
    else null
}


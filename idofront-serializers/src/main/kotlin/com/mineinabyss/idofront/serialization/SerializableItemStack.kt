@file:UseSerializers(MiniMessageSerializer::class)

package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.di.DI
import com.mineinabyss.idofront.items.asColorable
import com.mineinabyss.idofront.messaging.idofrontLogger
import com.mineinabyss.idofront.nms.hideAttributeTooltipWithItemFlagSet
import com.mineinabyss.idofront.plugin.Plugins
import com.mineinabyss.idofront.serialization.recipes.options.IngredientOption
import dev.lone.itemsadder.api.CustomStack
import io.lumine.mythiccrucible.MythicCrucible
import io.th0rgal.oraxen.OraxenPlugin
import io.th0rgal.oraxen.api.OraxenItems
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.EncodeDefault.Mode.NEVER
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.*
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemRarity
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.KnowledgeBookMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.inventory.meta.components.FoodComponent
import org.bukkit.inventory.meta.components.JukeboxPlayableComponent
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
    @EncodeDefault(NEVER) val customModelData: Int? = null,
    @EncodeDefault(NEVER) val itemName: Component? = null,
    // This is private as we only want to use itemName in configs
    @EncodeDefault(NEVER) private val customName: Component? = null,
    @EncodeDefault(NEVER) val lore: List<Component>? = null,
    @EncodeDefault(NEVER) val unbreakable: Boolean? = null,
    @EncodeDefault(NEVER) val damage: Int? = null,
    @EncodeDefault(NEVER) val durability: Int? = null,
    @EncodeDefault(NEVER) val prefab: String? = null,
    @EncodeDefault(NEVER) val enchantments: List<SerializableEnchantment>? = null,
    @EncodeDefault(NEVER) val itemFlags: List<ItemFlag>? = null,
    @EncodeDefault(NEVER) val attributeModifiers: List<SerializableAttribute>? = null,
    @EncodeDefault(NEVER) val basePotionType: @Serializable(with = PotionTypeSerializer::class) PotionType? = null,
    @EncodeDefault(NEVER) val customPotionEffects: List<@Serializable(with = PotionEffectSerializer::class) PotionEffect> = listOf(),
    @EncodeDefault(NEVER) val knowledgeBookRecipes: List<String>? = null,
    @EncodeDefault(NEVER) val color: @Serializable(with = ColorSerializer::class) Color? = null,
    @EncodeDefault(NEVER) val food: @Serializable(with = FoodComponentSerializer::class) FoodComponent? = null,
    @EncodeDefault(NEVER) val jukeboxPlayable: @Serializable(with = JukeboxPlayableSerializer::class) JukeboxPlayableComponent? = null,
    @EncodeDefault(NEVER) val hideTooltip: Boolean? = null,
    @EncodeDefault(NEVER) val isFireResistant: Boolean? = null,
    @EncodeDefault(NEVER) val enchantmentGlintOverride: Boolean? = null,
    @EncodeDefault(NEVER) val maxStackSize: Int? = null,
    @EncodeDefault(NEVER) val rarity: ItemRarity? = null,

    // Custom recipes
    @EncodeDefault(NEVER) val tag: String? = null,
    @EncodeDefault(NEVER) val recipeOptions: List<IngredientOption> = listOf(),

    // Third-party plugins
    @EncodeDefault(NEVER) val crucibleItem: String? = null,
    @EncodeDefault(NEVER) val oraxenItem: String? = null,
    @EncodeDefault(NEVER) val itemsadderItem: String? = null,
) {

    /**
     * Converts this serialized item's data to an [ItemStack], optionally applying the changes to an
     * [existing item][applyTo].
     */
    fun toItemStack(applyTo: ItemStack = ItemStack(type ?: Material.AIR)): ItemStack {
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

        // Import ItemStack from Oraxen
        oraxenItem?.let { id ->
            if (Plugins.isEnabled<OraxenPlugin>()) {
                OraxenItems.getItemById(id)?.build()?.let {
                    applyTo.type = it.type
                    applyTo.itemMeta = it.itemMeta
                } ?: idofrontLogger.w("No Oraxen item found with id $id")
            } else {
                idofrontLogger.w("Tried to import Oraxen item, but Oraxen was not enabled")
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
        prefab?.let { encodePrefab.invoke(applyTo, it) }

        // Modify item
        amount?.let(applyTo::setAmount)
        type?.let(applyTo::setType)

        // Modify meta
        applyTo.editMeta { meta ->
            itemName?.let(meta::itemName)
            customName?.let(meta::displayName)
            customModelData?.let(meta::setCustomModelData)
            lore?.let { meta.lore(it.map { l -> l.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE) }) }

            unbreakable?.let(meta::setUnbreakable)
            damage?.let { (meta as? Damageable)?.damage = damage }
            durability?.let { (meta as? Damageable)?.setMaxDamage(it) }
            itemFlags?.let { meta.addItemFlags(*itemFlags.toTypedArray()) }
            color?.let { meta.asColorable()?.color = color }
            basePotionType?.let { (meta as? PotionMeta)?.basePotionType = basePotionType }
            customPotionEffects.forEach { (meta as? PotionMeta)?.addCustomEffect(it, true) }
            enchantments?.forEach { meta.addEnchant(it.enchant, it.level, true) }
            attributeModifiers?.forEach { meta.addAttributeModifier(it.attribute, it.modifier) }

            knowledgeBookRecipes?.let { (meta as? KnowledgeBookMeta)?.recipes = knowledgeBookRecipes.map { it.getSubRecipeIDs() }.flatten() }

            enchantmentGlintOverride?.let(meta::setEnchantmentGlintOverride)
            food?.let(meta::setFood)
            jukeboxPlayable?.let(meta::setJukeboxPlayable)
            maxStackSize?.let(meta::setMaxStackSize)
            rarity?.let(meta::setRarity)
            isFireResistant?.let(meta::setFireResistant)
            hideTooltip?.let(meta::setHideTooltip)
        }

        applyTo.hideAttributeTooltipWithItemFlagSet()
        return applyTo
    }

    fun toItemStackOrNull(applyTo: ItemStack = ItemStack(type ?: Material.AIR)) =
        toItemStack(applyTo).takeIf { it.type != Material.AIR }

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
        customModelData = if (hasCustomModelData()) customModelData else null,
        itemName = if (hasItemName()) itemName() else null,
        customName = if (hasDisplayName()) displayName() else null,
        unbreakable = isUnbreakable.takeIf { it },
        lore = if (this.hasLore()) this.lore() else null,
        damage = (this as? Damageable)?.takeIf { it.hasDamage() }?.damage,
        durability = (this as? Damageable)?.takeIf { it.hasMaxDamage() }?.maxDamage,
        enchantments = enchants.takeIf { it.isNotEmpty() }?.map { SerializableEnchantment(it.key, it.value) },
        knowledgeBookRecipes = ((this as? KnowledgeBookMeta)?.recipes?.map { it.getItemPrefabFromRecipe() }?.flatten()
            ?: emptyList()).takeIf { it.isNotEmpty() },
        itemFlags = (this?.itemFlags?.toList() ?: listOf()).takeIf { it.isNotEmpty() },
        attributeModifiers = attributeList.takeIf { it.isNotEmpty() },
        basePotionType = (this as? PotionMeta)?.basePotionType,
        color = (this as? PotionMeta)?.color ?: (this as? LeatherArmorMeta)?.color,
        food = if (hasFood()) food else null,
        jukeboxPlayable = if (hasJukeboxPlayable()) jukeboxPlayable else null,
        enchantmentGlintOverride = if (hasEnchantmentGlintOverride()) enchantmentGlintOverride else null,
        maxStackSize = if (hasMaxStackSize()) maxStackSize else null,
        rarity = if (hasRarity()) rarity else null,
        hideTooltip = isHideTooltip.takeIf { it },
        isFireResistant = isFireResistant.takeIf { it },

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


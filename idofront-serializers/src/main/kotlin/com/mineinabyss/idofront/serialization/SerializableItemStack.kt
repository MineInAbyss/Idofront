@file:UseSerializers(MiniMessageSerializer::class)

package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.di.DI
import com.mineinabyss.idofront.messaging.idofrontLogger
import com.mineinabyss.idofront.nms.hideAttributeTooltipWithItemFlagSet
import com.mineinabyss.idofront.plugin.Plugins
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
import org.bukkit.potion.PotionType
import java.util.*

typealias SerializableItemStack = @Serializable(with = SerializableItemStackSerializer::class) BaseSerializableItemStack
typealias SerializableItemStackProperties = BaseSerializableItemStack.Properties

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
    @EncodeDefault(NEVER) private val displayName: Component? = null,
    @EncodeDefault(NEVER) val lore: List<Component>? = null,
    @EncodeDefault(NEVER) val unbreakable: Boolean? = null,
    @EncodeDefault(NEVER) val damage: Int? = null,
    @EncodeDefault(NEVER) val durability: Int? = null,
    @EncodeDefault(NEVER) val prefab: String? = null,
    @EncodeDefault(NEVER) val enchantments: List<SerializableEnchantment>? = null,
    @EncodeDefault(NEVER) val itemFlags: List<ItemFlag>? = null,
    @EncodeDefault(NEVER) val attributeModifiers: List<SerializableAttribute>? = null,
    @EncodeDefault(NEVER) val potionType: @Serializable(with = PotionTypeSerializer::class) PotionType? = null,
    @EncodeDefault(NEVER) val knowledgeBookRecipes: List<String>? = null,
    @EncodeDefault(NEVER) val color: @Serializable(with = ColorSerializer::class) Color? = null,
    @EncodeDefault(NEVER) val food: @Serializable(with = FoodComponentSerializer::class) FoodComponent? = null,
    @EncodeDefault(NEVER) val hideTooltips: Boolean? = null,
    @EncodeDefault(NEVER) val isFireResistant: Boolean? = null,
    @EncodeDefault(NEVER) val enchantmentGlintOverride: Boolean? = null,
    @EncodeDefault(NEVER) val maxStackSize: Int? = null,
    @EncodeDefault(NEVER) val rarity: ItemRarity? = null,
    @EncodeDefault(NEVER) val tag: String? = null,

    // Third-party plugins
    @EncodeDefault(NEVER) val crucibleItem: String? = null,
    @EncodeDefault(NEVER) val oraxenItem: String? = null,
    @EncodeDefault(NEVER) val itemsadderItem: String? = null,
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
        prefab?.takeIf { Properties.PREFAB !in ignoreProperties }?.let { encodePrefab.invoke(applyTo, it) }

        // Modify item
        amount?.takeIf { Properties.AMOUNT !in ignoreProperties }?.let { applyTo.amount = it }
        type?.takeIf { Properties.TYPE !in ignoreProperties }?.let { applyTo.type = it }

        // Modify meta
        val meta = applyTo.itemMeta ?: return applyTo
        customModelData?.takeIf { Properties.CUSTOM_MODEL_DATA !in ignoreProperties }
            ?.let { meta.setCustomModelData(it) }
        itemName?.takeIf { Properties.ITEM_NAME !in ignoreProperties }
            ?.let { meta.itemName(it) }
        displayName?.takeIf { Properties.DISPLAY_NAME !in ignoreProperties }
            ?.let { meta.displayName(it) }
        unbreakable?.takeIf { Properties.UNBREAKABLE !in ignoreProperties }
            ?.let { meta.isUnbreakable = it }
        lore?.takeIf { Properties.LORE !in ignoreProperties }
            ?.let { meta.lore(it.map { line -> line.removeItalics() }) }
        damage?.takeIf { meta is Damageable && Properties.DAMAGE !in ignoreProperties }?.let {
            (meta as Damageable).damage = it
        }
        durability?.takeIf { meta is Damageable && Properties.DURABILITY !in ignoreProperties }?.let {
            (meta as Damageable).setMaxDamage(it)
        }
        if (itemFlags?.isNotEmpty() == true && Properties.ITEM_FLAGS !in ignoreProperties) meta.addItemFlags(*itemFlags.toTypedArray())
        if (color != null && Properties.COLOR !in ignoreProperties)
            (meta as? PotionMeta)?.setColor(color) ?: (meta as? LeatherArmorMeta)?.setColor(color)
        if (potionType != null && Properties.POTION_TYPE !in ignoreProperties)
            (meta as? PotionMeta)?.basePotionType = potionType
        if (enchantments != null && Properties.ENCHANTMENTS !in ignoreProperties)
            enchantments.forEach { meta.addEnchant(it.enchant, it.level, true) }
        if (knowledgeBookRecipes != null && Properties.KNOWLEDGE_BOOK_RECIPES !in ignoreProperties)
            (meta as? KnowledgeBookMeta)?.recipes = knowledgeBookRecipes.map { it.getSubRecipeIDs() }.flatten()
        if (attributeModifiers != null && Properties.ATTRIBUTE_MODIFIERS !in ignoreProperties)
            attributeModifiers.forEach { meta.addAttributeModifier(it.attribute, it.modifier) }

        enchantmentGlintOverride?.takeIf { Properties.ENCHANTMENT_GLINT_OVERRIDE !in ignoreProperties }?.let { meta.setEnchantmentGlintOverride(it) }
        if (Properties.FOOD !in ignoreProperties) meta.setFood(food)
        if (Properties.ENCHANTMENT_GLINT_OVERRIDE !in ignoreProperties) meta.setEnchantmentGlintOverride(enchantmentGlintOverride)
        if (Properties.MAX_STACK_SIZE !in ignoreProperties) meta.setMaxStackSize(maxStackSize)
        rarity?.takeIf { Properties.ITEM_RARITY !in ignoreProperties }?.let { meta.setRarity(it) }
        isFireResistant?.takeIf { Properties.FIRE_RESISTANT !in ignoreProperties }?.let { meta.isFireResistant = it }
        hideTooltips?.takeIf { Properties.HIDE_TOOLTIPS !in ignoreProperties }?.let { meta.isHideTooltip = it }

        applyTo.itemMeta = meta
        return applyTo.hideAttributeTooltipWithItemFlagSet()
    }

    fun toItemStackOrNull(applyTo: ItemStack = ItemStack(type ?: Material.AIR)) =
        toItemStack(applyTo).takeIf { it.type != Material.AIR }


    enum class Properties {
        TYPE,
        AMOUNT,
        CUSTOM_MODEL_DATA,
        ITEM_NAME,
        DISPLAY_NAME,
        LORE,
        UNBREAKABLE,
        DAMAGE,
        DURABILITY,
        PREFAB,
        ENCHANTMENTS,
        ITEM_FLAGS,
        ATTRIBUTE_MODIFIERS,
        POTION_TYPE,
        KNOWLEDGE_BOOK_RECIPES,
        COLOR,
        FOOD,
        HIDE_TOOLTIPS,
        FIRE_RESISTANT,
        ENCHANTMENT_GLINT_OVERRIDE,
        MAX_STACK_SIZE,
        ITEM_RARITY,
    }

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
        displayName = if (hasDisplayName()) displayName() else null,
        unbreakable = isUnbreakable.takeIf { it },
        lore = if (this.hasLore()) this.lore() else null,
        damage = (this as? Damageable)?.takeIf { it.hasDamage() }?.damage,
        durability = (this as? Damageable)?.takeIf { it.hasMaxDamage() }?.maxDamage,
        enchantments = enchants.map { SerializableEnchantment(it.key, it.value) }.takeIf { it.isNotEmpty() },
        knowledgeBookRecipes = ((this as? KnowledgeBookMeta)?.recipes?.map { it.getItemPrefabFromRecipe() }?.flatten()
            ?: emptyList()).takeIf { it.isNotEmpty() },
        itemFlags = (this?.itemFlags?.toList() ?: listOf()).takeIf { it.isNotEmpty() },
        attributeModifiers = attributeList.takeIf { it.isNotEmpty() },
        potionType = (this as? PotionMeta)?.basePotionType,
        color = (this as? PotionMeta)?.color ?: (this as? LeatherArmorMeta)?.color,
        food = if (hasFood()) food else null,
        enchantmentGlintOverride = if (hasEnchantmentGlintOverride()) enchantmentGlintOverride else null,
        maxStackSize = if (hasMaxStackSize()) maxStackSize else null,
        rarity = if (hasRarity()) rarity else null,
        hideTooltips = isHideTooltip,
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
        if (recipe !is Keyed) return@forEachRemaining
        if (recipe.key.namespace == NamespacedKey.MINECRAFT_NAMESPACE) {
            recipes.add(recipe.key.asString())
        } else if (recipe.key == this) {
            recipes.add(recipe.key.asString().dropLast(1))
        }
    }
    return recipes
}


package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.serialization.SerializableDataTypes.ConsumeEffect.ClearAllEffectsConsumeEffect.toSerializable
import com.mineinabyss.idofront.serialization.SerializableDataTypes.Profile.ProfileProperty
import com.nexomc.nexo.utils.ticks
import io.papermc.paper.datacomponent.DataComponentType
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.*
import io.papermc.paper.datacomponent.item.Equippable.equippable
import io.papermc.paper.datacomponent.item.MapDecorations.DecorationEntry
import io.papermc.paper.datacomponent.item.Tool.Rule
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.TypedKey
import io.papermc.paper.registry.set.RegistrySet
import io.papermc.paper.registry.tag.TagKey
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.EncodeDefault.Mode.NEVER
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.util.TriState
import org.bukkit.Art
import org.bukkit.Color
import org.bukkit.Registry
import org.bukkit.Sound
import org.bukkit.entity.EntityType
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.trim.ArmorTrim
import org.bukkit.map.MapCursor
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.DurationUnit


@Suppress("UnstableApiUsage")
object SerializableDataTypes {

    fun <T : Any> setData(itemStack: ItemStack, dataComponent: DataComponentType.Valued<T>, any: T?) {
        any?.let { itemStack.setData(dataComponent, any) }
    }

    fun <T> setData(itemStack: ItemStack, dataComponent: DataComponentType.NonValued, any: T?) {
        any?.let { itemStack.setData(dataComponent) }
    }

    interface DataType {
        fun setDataType(itemStack: ItemStack)
    }

    @Serializable
    class CustomModelData(
        @EncodeDefault(NEVER) val floats: List<Float> = listOf(),
        @EncodeDefault(NEVER) val flags: List<Boolean> = listOf(),
        @EncodeDefault(NEVER) val strings: List<String> = listOf(),
        @EncodeDefault(NEVER) val colors: List<@Serializable(ColorSerializer::class) Color> = listOf()
    ) : DataType {
        constructor(customModelData: io.papermc.paper.datacomponent.item.CustomModelData) : this(
            customModelData.floats(),
            customModelData.flags(),
            customModelData.strings(),
            customModelData.colors()
        )

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.CUSTOM_MODEL_DATA,
                io.papermc.paper.datacomponent.item.CustomModelData.customModelData()
                    .addFloats(floats).addFlags(flags).addStrings(strings).addColors(colors)
            )
        }
    }

    @Serializable
    data class PotionContents(
        val potionType: @Serializable(PotionTypeSerializer::class) org.bukkit.potion.PotionType? = null,
        val color: @Serializable(ColorSerializer::class) Color? = null,
        val customEffects: List<@Serializable(PotionEffectSerializer::class) PotionEffect> = emptyList(),
        val customName: String? = null,
    ) : DataType {

        constructor(potionContents: io.papermc.paper.datacomponent.item.PotionContents) : this(
            potionContents.potion(),
            potionContents.customColor(),
            potionContents.customEffects(),
            potionContents.customName()
        )

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.POTION_CONTENTS,
                io.papermc.paper.datacomponent.item.PotionContents.potionContents().potion(potionType).customColor(color)
                    .addCustomEffects(customEffects).customName(customName).build()
            )
        }
    }

    @Serializable
    @JvmInline
    value class Enchantments( @EncodeDefault(NEVER) val enchantments: List<SerializableEnchantment> = listOf()) : DataType {
        constructor(itemEnchantments: ItemEnchantments) : this(itemEnchantments.enchantments().map(::SerializableEnchantment))

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.ENCHANTMENTS,
                ItemEnchantments.itemEnchantments(enchantments.associate { it.enchant to it.level })
            )
        }
    }

    @Serializable
    @JvmInline
    value class StoredEnchantments(
        val enchantments: List<SerializableEnchantment>,
    ) : DataType {
        constructor(itemEnchantments: ItemEnchantments) : this(
            itemEnchantments.enchantments().map(::SerializableEnchantment)
        )

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.STORED_ENCHANTMENTS,
                ItemEnchantments.itemEnchantments(enchantments.associate { it.enchant to it.level })
            )
        }
    }

    @Serializable
    @JvmInline
    value class ChargedProjectiles(private val projectiles: List<SerializableItemStack>) : DataType {
        constructor(vararg projectiles: ItemStack) : this(projectiles.map { it.toSerializable() })
        constructor(projectiles: io.papermc.paper.datacomponent.item.ChargedProjectiles) : this(
            projectiles.projectiles().map { it.toSerializable() })

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.CHARGED_PROJECTILES,
                io.papermc.paper.datacomponent.item.ChargedProjectiles.chargedProjectiles(projectiles.mapNotNull { it.toItemStackOrNull() })
            )
        }
    }

    @Serializable
    @JvmInline
    value class BundleContent(private val contents: List<SerializableItemStack>) : DataType {
        constructor(vararg contents: ItemStack) : this(contents.map { it.toSerializable() })
        constructor(contents: BundleContents) : this(contents.contents().map { it.toSerializable() })

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.BUNDLE_CONTENTS,
                BundleContents.bundleContents(contents.mapNotNull { it.toItemStackOrNull() })
            )
        }
    }

    @Serializable
    data class WrittenBook(
        val title: String,
        val author: String,
        val generation: Int,
        val resolved: Boolean,
        val pages: List<@Serializable(MiniMessageSerializer::class) Component> = emptyList()
    ) : DataType {
        constructor(written: WrittenBookContent) : this(
            written.title().raw(),
            written.author(),
            written.generation(),
            written.resolved(),
            written.pages().map { it.raw() }
        )

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.WRITTEN_BOOK_CONTENT,
                WrittenBookContent.writtenBookContent(title, author).resolved(resolved).generation(generation)
                    .addPages(pages).build()
            )
        }
    }

    @Serializable
    data class WritableBook(val pages: List<String>) : DataType {
        constructor(writable: WritableBookContent) : this(writable.pages().map { it.raw() })

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.WRITABLE_BOOK_CONTENT,
                WritableBookContent.writeableBookContent().addPages(pages).build()
            )
        }
    }

    @Serializable
    data class MapDecoration(val type: MapCursor.Type, val x: Double, val z: Double, val rotation: Float) {
        constructor(entry: DecorationEntry) : this(entry.type(), entry.x(), entry.z(), entry.rotation())
        val paperDecoration: DecorationEntry = MapDecorations.decorationEntry(type, x, z, rotation)

        companion object {
            fun toPaperDecorations(decorations: List<MapDecoration>) =
                decorations.mapIndexed { i, mapDecoration -> i.toString() to mapDecoration.paperDecoration }.toMap()
        }
    }

    @Serializable
    @JvmInline
    value class Enchantable(val enchantable: Int) : DataType {
        constructor(enchantable: io.papermc.paper.datacomponent.item.Enchantable) : this(enchantable.value())

        override fun setDataType(itemStack: ItemStack) {
            val enchantable = runCatching { io.papermc.paper.datacomponent.item.Enchantable.enchantable(enchantable) }.getOrNull()
            if (enchantable != null) itemStack.setData(DataComponentTypes.ENCHANTABLE, enchantable)
            itemStack.unsetData(DataComponentTypes.ENCHANTABLE)
        }
    }

    @Serializable
    @JvmInline
    value class Repairable(val repairable: List<@Serializable(KeySerializer::class) Key>) : DataType {
        constructor(repairable: io.papermc.paper.datacomponent.item.Repairable) :
                this(repairable.types().resolve(Registry.ITEM).map { it.key() })

        override fun setDataType(itemStack: ItemStack) {
            val items = repairable.mapNotNull { Registry.ITEM.get(it)?.key()?.let { TypedKey.create(RegistryKey.ITEM, it) } }
            val tags = repairable.mapNotNull {
                runCatching { Registry.ITEM.getTag(TagKey.create(RegistryKey.ITEM, it)) }.getOrNull()?.values()
            }.flatten()

            itemStack.setData(DataComponentTypes.REPAIRABLE, io.papermc.paper.datacomponent.item.Repairable.repairable(
                RegistrySet.keySet(RegistryKey.ITEM, items.plus(tags).filterNotNull().toMutableList())
            ))
        }
    }

    @Serializable
    data class Tool(
        val rules: List<Rule> = emptyList(),
        val defaultMiningSpeed: Float = 1f,
        val damagePerBlock: Int = 1,
        val canDestroyBlocksInCreative: Boolean = true
    ) : BlockTags(), DataType {
        constructor(tool: io.papermc.paper.datacomponent.item.Tool) : this(
            tool.rules().map(::Rule),
            tool.defaultMiningSpeed(),
            tool.damagePerBlock(),
            tool.canDestroyBlocksInCreative()
        )

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.TOOL, io.papermc.paper.datacomponent.item.Tool.tool()
                    .damagePerBlock(damagePerBlock)
                    .defaultMiningSpeed(defaultMiningSpeed)
                    .addRules(rules.toPaperRules())
                    .canDestroyBlocksInCreative(canDestroyBlocksInCreative)
                    .build()
            )
        }

        @Serializable
        data class Rule(
            val blockTypes: List<@Serializable(KeySerializer::class) Key>,
            val speed: Float? = null,
            val correctForDrops: @Serializable(TriStateSerializer::class) TriState = TriState.NOT_SET
        ) {
            constructor(rule: io.papermc.paper.datacomponent.item.Tool.Rule) : this(
                rule.blocks().map { it.key() },
                rule.speed(),
                rule.correctForDrops()
            )
        }
    }

    @Serializable
    data class Weapon(
        val damagePerAttack: Int = 1,
        val disableBlockingDuration: @Serializable(DurationSerializer::class) Duration = Duration.ZERO,
    ): DataType {
        constructor(weapon: io.papermc.paper.datacomponent.item.Weapon) : this(
            weapon.itemDamagePerAttack(),
            weapon.disableBlockingForSeconds().toDuration(DurationUnit.SECONDS)
        )

        init {
            require(damagePerAttack >= 0) { "damagePerAttack must not be negative" }
            require(disableBlockingDuration.inWholeSeconds >= 0) { "disableBlockingDuration must not be negative" }
        }

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.WEAPON, io.papermc.paper.datacomponent.item.Weapon.weapon()
                    .itemDamagePerAttack(damagePerAttack)
                    .disableBlockingForSeconds(disableBlockingDuration.inWholeSeconds.toFloat())
                    .build()
            )
        }

    }

    @Serializable
    data class BlocksAttacks(
        val blockDelayDuration: @Serializable(DurationSerializer::class) Duration = Duration.ZERO,
        val disableCooldownScale: Float = 1f,
        val damageReduction: List<DamageReduction> = listOf(),
        val itemDamage: ItemDamageFunction = ItemDamageFunction(),
        val bypassedBy: @Serializable(KeySerializer::class) Key? = null,
        val blockSound: @Serializable(KeySerializer::class) Key? = null,
        val disableSound: @Serializable(KeySerializer::class) Key? = null,
    ): DataType {
        constructor(blocksAttacks: io.papermc.paper.datacomponent.item.BlocksAttacks) : this(
            blocksAttacks.blockDelaySeconds().toDuration(DurationUnit.SECONDS),
            blocksAttacks.disableCooldownScale(),
            blocksAttacks.damageReductions().map { DamageReduction(it) },
            ItemDamageFunction(blocksAttacks.itemDamage()),
            blocksAttacks.bypassedBy()?.key(),
            blocksAttacks.blockSound(),
            blocksAttacks.disableSound()
        )

        init {
            require(blockDelayDuration.inWholeSeconds >= 0) { "blockDelayDuration must not be negative" }
            require(disableCooldownScale >= 0f) { "disableCooldownScale must not be negative" }
        }

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.BLOCKS_ATTACKS, io.papermc.paper.datacomponent.item.BlocksAttacks.blocksAttacks()
                    .blockDelaySeconds(blockDelayDuration.inWholeSeconds.toFloat())
                    .disableCooldownScale(disableCooldownScale)
                    .damageReductions(damageReduction.map { it.toPaper() })
                    .itemDamage(itemDamage.toPaper())
                    .bypassedBy(bypassedBy?.let { TagKey.create(RegistryKey.DAMAGE_TYPE, it) })
                    .blockSound(blockSound)
                    .disableSound(disableSound)
                    .build()
            )
        }

        @Serializable
        class DamageReduction(val horizontalBlockingAngle: Float, val type: List<@Serializable(KeySerializer::class) Key> = listOf(), val base: Float = 0f, val factor: Float = 1f) {

            constructor(damageReduction: io.papermc.paper.datacomponent.item.blocksattacks.DamageReduction) : this(
                damageReduction.horizontalBlockingAngle(), damageReduction.type()?.map { it.key() } ?: listOf(),
                damageReduction.base(), damageReduction.factor()
            )

            init {
                require(horizontalBlockingAngle > 0f) { "horizontalBlockingAngle must be positive" }
            }

            fun toPaper(): io.papermc.paper.datacomponent.item.blocksattacks.DamageReduction {
                return io.papermc.paper.datacomponent.item.blocksattacks.DamageReduction.damageReduction()
                    .horizontalBlockingAngle(horizontalBlockingAngle).base(base).factor(factor)
                    .type(RegistrySet.keySet(RegistryKey.DAMAGE_TYPE, type.map { TypedKey.create(RegistryKey.DAMAGE_TYPE, it) }))
                    .build()
            }
        }

        @Serializable
        class ItemDamageFunction(val threshold: Float = 1f, val base: Float = 0f, val factor: Float = 1f) {

            constructor(damageFunction: io.papermc.paper.datacomponent.item.blocksattacks.ItemDamageFunction) : this(
                damageFunction.threshold(), damageFunction.base(), damageFunction.factor()
            )

            init {
                require(threshold >= 0) { "threshold must not be negative" }
            }

            fun toPaper(): io.papermc.paper.datacomponent.item.blocksattacks.ItemDamageFunction {
                return io.papermc.paper.datacomponent.item.blocksattacks.ItemDamageFunction.itemDamageFunction()
                    .threshold(threshold).base(base).factor(factor).build()
            }
        }

    }

    @Serializable
    data class CanPlaceOn(
        val modifiers: List<BlockPredicate>
    ) : BlockTags(), DataType {
        constructor(predicate: ItemAdventurePredicate) : this(predicate.predicates().map(::BlockPredicate))

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.CAN_PLACE_ON, ItemAdventurePredicate.itemAdventurePredicate().apply {
                    modifiers.toPaperBlockPredicate().forEach { blockPredicate ->
                        addPredicate(blockPredicate)
                    }
                }.build()
            )
        }
    }

    @Serializable
    data class CanBreak(
        val modifiers: List<BlockPredicate>
    ) : BlockTags(), DataType {
        constructor(predicate: ItemAdventurePredicate) : this(predicate.predicates().map(::BlockPredicate))

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.CAN_BREAK, ItemAdventurePredicate.itemAdventurePredicate().apply {
                    modifiers.toPaperBlockPredicate().forEach { blockPredicate ->
                        addPredicate(blockPredicate)
                    }
                }.build()
            )
        }
    }

    @Serializable
    sealed class BlockTags {
        fun List<Tool.Rule>.toPaperRules(): List<Rule> {
            val rules = mutableListOf<Rule>()

            this.forEach { rule ->
                val blockTagKeys =
                    rule.blockTypes.map { TagKey.create(RegistryKey.BLOCK, it) }.filter(Registry.BLOCK::hasTag)
                blockTagKeys.map(Registry.BLOCK::getTag).forEach { blockTag ->
                    rules += io.papermc.paper.datacomponent.item.Tool.rule(blockTag, rule.speed, rule.correctForDrops)
                }
                val blockKeys = rule.blockTypes.filter { Registry.BLOCK.get(it) != null }
                    .map { TypedKey.create(RegistryKey.BLOCK, it.key()) }
                val keySet = RegistrySet.keySet(RegistryKey.BLOCK, blockKeys)
                rules += io.papermc.paper.datacomponent.item.Tool.rule(keySet, rule.speed, rule.correctForDrops)
            }

            return rules
        }

        fun List<BlockPredicate>.toPaperBlockPredicate(): List<io.papermc.paper.block.BlockPredicate> {
            val blockPredicates = mutableListOf<io.papermc.paper.block.BlockPredicate>()

            this.forEach { blockPredicate ->
                blockPredicate.blocks
                    ?.map { TagKey.create(RegistryKey.BLOCK, it) }
                    ?.filter(Registry.BLOCK::hasTag)?.map(Registry.BLOCK::getTag)
                    ?.forEach { blockTag ->
                        blockPredicates += io.papermc.paper.block.BlockPredicate.predicate().blocks(blockTag).build()
                    }

                blockPredicate.blocks?.filter { Registry.BLOCK.get(it) != null }
                    ?.map { TypedKey.create(RegistryKey.BLOCK, it.key()) }
                    ?.let { blockKeys ->
                        val keySet = RegistrySet.keySet(RegistryKey.BLOCK, blockKeys)
                        blockPredicates += io.papermc.paper.block.BlockPredicate.predicate().blocks(keySet).build()
                    }
            }

            return blockPredicates
        }

        @Serializable
        data class BlockPredicate(
            val blocks: List<@Serializable(KeySerializer::class) Key>?
        ) {
            constructor(blockPredicate: io.papermc.paper.block.BlockPredicate) : this(
                blockPredicate.blocks()?.map { it.key() })
        }
    }

    @Serializable
    sealed interface ConsumeEffect {

        fun io.papermc.paper.datacomponent.item.consumable.ConsumeEffect.toSerializable() = when (this) {
            is io.papermc.paper.datacomponent.item.consumable.ConsumeEffect.ApplyStatusEffects -> ApplyEffectsConsumeEffect(effects(), probability())
            is io.papermc.paper.datacomponent.item.consumable.ConsumeEffect.RemoveStatusEffects -> RemoveEffectsConsumeEffect(removeEffects().resolve(Registry.POTION_EFFECT_TYPE).toList())
            is io.papermc.paper.datacomponent.item.consumable.ConsumeEffect.TeleportRandomly -> TeleportConsumeEffect(diameter())
            is io.papermc.paper.datacomponent.item.consumable.ConsumeEffect.PlaySound -> PlaySoundConsumeEffect(sound())
            is io.papermc.paper.datacomponent.item.consumable.ConsumeEffect.ClearAllStatusEffects -> ClearAllEffectsConsumeEffect
            else -> ApplyEffectsConsumeEffect(emptyList(), 0f)
        }

        fun toPaperEffect() : io.papermc.paper.datacomponent.item.consumable.ConsumeEffect {
            return when (this) {
                is ApplyEffectsConsumeEffect -> io.papermc.paper.datacomponent.item.consumable.ConsumeEffect.applyStatusEffects(effects, probability)
                is RemoveEffectsConsumeEffect -> io.papermc.paper.datacomponent.item.consumable.ConsumeEffect.removeEffects(RegistrySet.keySetFromValues(RegistryKey.MOB_EFFECT, effects))
                is TeleportConsumeEffect -> io.papermc.paper.datacomponent.item.consumable.ConsumeEffect.teleportRandomlyEffect(diameter)
                is PlaySoundConsumeEffect -> io.papermc.paper.datacomponent.item.consumable.ConsumeEffect.playSoundConsumeEffect(key)
                is ClearAllEffectsConsumeEffect -> io.papermc.paper.datacomponent.item.consumable.ConsumeEffect.clearAllStatusEffects()
            }
        }

        @Serializable
        @SerialName("APPLY")
        data class ApplyEffectsConsumeEffect(
            val effects: List<@Serializable(PotionEffectSerializer::class) PotionEffect>,
            val probability: Float = 1.0f
        ): ConsumeEffect

        @Serializable
        @SerialName("REMOVE")
        data class RemoveEffectsConsumeEffect(val effects: List<@Serializable(PotionEffectTypeSerializer::class) PotionEffectType>): ConsumeEffect

        @Serializable
        @SerialName("TELEPORT")
        data class TeleportConsumeEffect(val diameter: Float = 16.0f): ConsumeEffect

        @Serializable
        @SerialName("SOUND")
        data class PlaySoundConsumeEffect(val key: @Serializable(KeySerializer::class) Key) : ConsumeEffect

        @Serializable
        @SerialName("CLEAR")
        object ClearAllEffectsConsumeEffect : ConsumeEffect, io.papermc.paper.datacomponent.item.consumable.ConsumeEffect.ClearAllStatusEffects
    }

    @Serializable
    @JvmInline
    value class UseRemainder(val useRemainder: SerializableItemStack) : DataType {

        init {
            require(useRemainder.toItemStackOrNull() != null) { "UseRemainder cannot be null"}
            require(!useRemainder.toItemStack().isEmpty) { "UseRemainder cannot be empty, was $useRemainder" }
        }

        constructor(useRemainder: io.papermc.paper.datacomponent.item.UseRemainder) : this(useRemainder.transformInto().toSerializable())

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.USE_REMAINDER,
                io.papermc.paper.datacomponent.item.UseRemainder.useRemainder(useRemainder.toItemStack())
            )
        }
    }

    @Serializable
    @JvmInline
    value class DamageResistant(val damageResistant: @Serializable(KeySerializer::class) Key) : DataType {
        constructor(damageResistant: io.papermc.paper.datacomponent.item.DamageResistant) : this(damageResistant.types().key())

        private val damageTypeRegistry get() = RegistryAccess.registryAccess().getRegistry(RegistryKey.DAMAGE_TYPE)

        init {
            require(damageTypeRegistry.get(damageResistant) != null) { "${damageResistant.asString()} was not a valid DamageType"}
        }

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(DataComponentTypes.DAMAGE_RESISTANT, io.papermc.paper.datacomponent.item.DamageResistant.damageResistant(TagKey.create(RegistryKey.DAMAGE_TYPE, damageResistant)))
        }
    }

    @Serializable
    class DeathProtection(val deathEffects: List<ConsumeEffect> = listOf()) : DataType {
        constructor(deathProtection: io.papermc.paper.datacomponent.item.DeathProtection) : this(deathProtection.deathEffects().map { it.toSerializable() })

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(DataComponentTypes.DEATH_PROTECTION, io.papermc.paper.datacomponent.item.DeathProtection.deathProtection(deathEffects.map { it.toPaperEffect() }))
        }
    }

    @Serializable
    data class Consumable(
        val duration: @Serializable(DurationSerializer::class) Duration = 1.6f.toDuration(DurationUnit.SECONDS),
        val sound: @Serializable(KeySerializer::class) Key? = Registry.SOUNDS.getKey(Sound.ENTITY_GENERIC_EAT),
        val animation: ItemUseAnimation = ItemUseAnimation.EAT,
        val particles: Boolean = true,
        val consumeEffects: List<ConsumeEffect> = listOf()
    ) : DataType {
        constructor(consumable: io.papermc.paper.datacomponent.item.Consumable) : this(
            consumable.consumeSeconds().toDuration(DurationUnit.SECONDS),
            consumable.sound(),
            consumable.animation(),
            consumable.hasConsumeParticles(),
            consumable.consumeEffects().map { it.toSerializable() }
        )

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.CONSUMABLE, io.papermc.paper.datacomponent.item.Consumable.consumable()
                    .consumeSeconds(duration.toFloat(DurationUnit.SECONDS))
                    .run { if(sound != null) sound(sound) else this }
                    .animation(animation)
                    .hasConsumeParticles(particles)
                    .addEffects(consumeEffects.map { it.toPaperEffect() })
            )
        }
    }

    @Serializable
    data class Food(
        val nutrition: Int,
        val saturation: Float,
        val canAlwaysEat: Boolean = false
    ) : DataType {

        init {
            require(nutrition >= 0) { "Nutrition must be a non-negative integer, was $nutrition" }
        }

        constructor(foodProperties: FoodProperties) : this(
            foodProperties.nutrition(),
            foodProperties.saturation(),
            foodProperties.canAlwaysEat()
        )

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(DataComponentTypes.FOOD, FoodProperties.food().nutrition(nutrition).saturation(saturation).canAlwaysEat(canAlwaysEat))
        }
    }

    @Serializable
    @JvmInline
    value class DyedColor(
        val color: @Serializable(ColorSerializer::class) Color,
    ) : DataType {
        constructor(dyedItemColor: DyedItemColor) : this(dyedItemColor.color())

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(color))
        }
    }

    @Serializable
    @JvmInline
    value class MapColor(
        private val color: @Serializable(ColorSerializer::class) Color,
    ) : DataType {
        constructor(mapItemColor: MapItemColor) : this(mapItemColor.color())

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(DataComponentTypes.MAP_COLOR, MapItemColor.mapItemColor().color(color).build())
        }

    }

    @Serializable
    data class Equippable(
        val slot: EquipmentSlot,
        val model: @Serializable(KeySerializer::class) Key? = null,
        val cameraOverlay: @Serializable(KeySerializer::class) Key? = null,
        val equipSound: @Serializable(KeySerializer::class) Key? = Registry.SOUNDS.getKey(Sound.ITEM_ARMOR_EQUIP_GENERIC),
        val allowedEntities: List<EntityType>? = null,
        val damageOnHurt: Boolean = true,
        val swappable: Boolean = true,
        val dispensable: Boolean = true,
        val equipOnInteract: Boolean = true,
        val canBeSheared: Boolean = true,
        val shearingsound: @Serializable(KeySerializer::class) Key? = Registry.SOUNDS.getKey(Sound.ITEM_SHEARS_SNIP)
    ) : DataType {
        constructor(equippable: io.papermc.paper.datacomponent.item.Equippable) : this(
            equippable.slot(), equippable.assetId(), equippable.cameraOverlay(), equippable.equipSound(),
            equippable.allowedEntities()?.resolve(Registry.ENTITY_TYPE)?.toList(),
            equippable.damageOnHurt(), equippable.swappable(), equippable.dispensable(),
            equippable.equipOnInteract(), equippable.canBeSheared(), equippable.shearSound()
        )

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(
                DataComponentTypes.EQUIPPABLE,
                io.papermc.paper.datacomponent.item.Equippable.equippable(slot).assetId(model).cameraOverlay(cameraOverlay)
                    .run { if(equipSound != null) equipSound(equipSound) else this }
                    .allowedEntities(allowedEntities?.let { RegistrySet.keySetFromValues(RegistryKey.ENTITY_TYPE, it) })
                    .damageOnHurt(damageOnHurt).swappable(swappable).dispensable(dispensable)
            )
        }
    }

    @Serializable
    class Trim(
        val material: @Serializable(KeySerializer::class) Key,
        val pattern: @Serializable(KeySerializer::class) Key,
    ) : DataType {
        constructor(trim: ItemArmorTrim) : this(
            RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL).getKey(trim.armorTrim().material)!!,
            RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_PATTERN).getKey(trim.armorTrim().pattern)!!
        )

        override fun setDataType(itemStack: ItemStack) {
            val trimMaterial = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_MATERIAL).get(material)
                ?: error("Invalid TrimMaterial: " + material.asString())
            val trimPattern = RegistryAccess.registryAccess().getRegistry(RegistryKey.TRIM_PATTERN).get(pattern)
                ?: error("Invalid TrimPattern: " + pattern.asString())
            itemStack.setData(
                DataComponentTypes.TRIM,
                ItemArmorTrim.itemArmorTrim(ArmorTrim(trimMaterial, trimPattern))
            )
        }

    }

    @Serializable
    @JvmInline
    value class JukeboxPlayable(val jukeboxSong: @Serializable(KeySerializer::class) Key) : DataType {
        constructor(jukeboxPlayable: io.papermc.paper.datacomponent.item.JukeboxPlayable) :
                this(jukeboxPlayable.jukeboxSong().key())

        override fun setDataType(itemStack: ItemStack) {
            val jukeboxRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.JUKEBOX_SONG)
            val jukeboxSong = jukeboxRegistry.get(jukeboxSong) ?: return
            itemStack.setData(
                DataComponentTypes.JUKEBOX_PLAYABLE,
                io.papermc.paper.datacomponent.item.JukeboxPlayable.jukeboxPlayable(jukeboxSong)
            )
        }
    }

    @Serializable
    @JvmInline
    value class AttributeModifiers( @EncodeDefault(NEVER) val attributes: List<SerializableAttribute>) : DataType {

        constructor(attributeModifiers: ItemAttributeModifiers) : this(attributeModifiers.modifiers().map(::SerializableAttribute))

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes().apply {
                attributes.forEach { addModifier(it.attribute, it.modifier) }
            }.build())
        }
    }

    @Serializable
    data class UseCooldown(
        val duration: @Serializable(DurationSerializer::class) Duration,
        val group: @Serializable(KeySerializer::class) Key? = null
    ) : DataType {

        init {
            require(duration >= Duration.ZERO) { "Seconds cannot be below 0" }
        }

        constructor(cooldown: io.papermc.paper.datacomponent.item.UseCooldown) : this(cooldown.seconds().toDuration(DurationUnit.SECONDS), cooldown.cooldownGroup())

        override fun setDataType(itemStack: ItemStack) {
            val seconds = duration.toFloat(DurationUnit.SECONDS)
            val useCooldown = io.papermc.paper.datacomponent.item.UseCooldown.useCooldown(seconds).cooldownGroup(group).build()
            itemStack.setData(DataComponentTypes.USE_COOLDOWN, useCooldown)
        }
    }

    @Serializable
    data class TooltipDisplay(val hideTooltip: Boolean = false, val hiddenComponents: List<@Serializable(KeySerializer::class) Key> = listOf()) : DataType {
        constructor(tooltipDisplay: io.papermc.paper.datacomponent.item.TooltipDisplay) : this(
            tooltipDisplay.hideTooltip(), tooltipDisplay.hiddenComponents().map { it.key() }
        )

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(DataComponentTypes.TOOLTIP_DISPLAY,
                io.papermc.paper.datacomponent.item.TooltipDisplay.tooltipDisplay().hideTooltip(hideTooltip)
                    .hiddenComponents(hiddenComponents.mapNotNullTo(mutableSetOf(), SerializableItemStack.dataComponentRegistry::get))
            )
        }
    }

    @Serializable
    @JvmInline
    value class PaintingVariant(val key: @Serializable(KeySerializer::class) Key) : DataType {
        companion object {
            private val registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.PAINTING_VARIANT)
        }

        constructor(art: Art) : this(registry.getKey(art)!!)

        init {
            require(registry.get(key) != null) { "No Painting-Variant found with key $key" }
        }

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(DataComponentTypes.PAINTING_VARIANT, registry.get(key)!!)
        }
    }

    @Serializable
    data class Profile(val name: String?, val uuid: @Serializable(UUIDSerializer::class) UUID?, val properties: List<ProfileProperty>) : DataType {

        constructor(profile: ResolvableProfile) : this(profile.name(), profile.uuid(), profile.properties().map(::ProfileProperty))

        override fun setDataType(itemStack: ItemStack) {
            val profile = ResolvableProfile.resolvableProfile()
                .name(name).uuid(uuid)
                .addProperties(properties.map { it.toPaper() })
            itemStack.setData(DataComponentTypes.PROFILE, profile.build())
        }

        @Serializable
        data class ProfileProperty(val name: String, val value: String, val signature: String? = null) {

            constructor(property: com.destroystokyo.paper.profile.ProfileProperty) : this(property.name, property.value, property.signature)

            fun toPaper(): com.destroystokyo.paper.profile.ProfileProperty {
                return com.destroystokyo.paper.profile.ProfileProperty(name, value, signature)
            }
        }
    }

    @Serializable
    data class KineticWeapon(
        val delay: @Serializable(DurationSerializer::class) Duration,
        val contactDelay: @Serializable(DurationSerializer::class) Duration,
        val forwardMovement: Float, val damageMultiplier: Float,
        val sound: Key?, val hitSound: Key?,
        val damageConditions: Condition?,
        val dismountConditions: Condition?,
        val knockbackConditions: Condition?
    ) : DataType {

        constructor(kineticWeapon: io.papermc.paper.datacomponent.item.KineticWeapon) : this(
            kineticWeapon.delayTicks().ticks, kineticWeapon.contactCooldownTicks().ticks,
            kineticWeapon.forwardMovement(), kineticWeapon.damageMultiplier(),
            kineticWeapon.sound(), kineticWeapon.hitSound(),
            kineticWeapon.damageConditions()?.let(::Condition),
            kineticWeapon.dismountConditions()?.let(::Condition),
            kineticWeapon.knockbackConditions()?.let(::Condition),
        )

        override fun setDataType(itemStack: ItemStack) {
            val kinetic = io.papermc.paper.datacomponent.item.KineticWeapon.kineticWeapon()
                .delayTicks(delay.ticks).contactCooldownTicks(contactDelay.ticks)
                .forwardMovement(forwardMovement).damageMultiplier(damageMultiplier)
                .sound(sound).hitSound(hitSound)
                .damageConditions(damageConditions?.toPaper())
                .dismountConditions(dismountConditions?.toPaper())
                .knockbackConditions(knockbackConditions?.toPaper())
            itemStack.setData(DataComponentTypes.KINETIC_WEAPON, kinetic.build())
        }

        @Serializable
        data class Condition(val maxDuration: @Serializable(DurationSerializer::class) Duration, val minSpeed: Float = 0f, val minRelativeSpeed: Float = 0f) {

            constructor(condition: io.papermc.paper.datacomponent.item.KineticWeapon.Condition) : this(
                condition.maxDurationTicks().ticks, condition.minSpeed(), condition.minRelativeSpeed()
            )

            fun toPaper(): io.papermc.paper.datacomponent.item.KineticWeapon.Condition {
                return io.papermc.paper.datacomponent.item.KineticWeapon.condition(maxDuration.ticks, minSpeed, minRelativeSpeed)
            }
        }

    }

    @Serializable
    data class PiercingWeapon(val dealsKnockback: Boolean = true, val dismounts: Boolean = false, val sound: Key?, val hitSound: Key?) : DataType {

        constructor(piercing: io.papermc.paper.datacomponent.item.PiercingWeapon) : this(
            piercing.dealsKnockback(), piercing.dismounts(), piercing.sound(), piercing.hitSound()
        )

        override fun setDataType(itemStack: ItemStack) {
            val piercing = io.papermc.paper.datacomponent.item.PiercingWeapon.piercingWeapon()
                .dealsKnockback(dealsKnockback).dismounts(dismounts).sound(sound).hitSound(hitSound)
            itemStack.setData(DataComponentTypes.PIERCING_WEAPON, piercing.build())
        }
    }

    @Serializable
    data class SwingAnimation(
        val type: io.papermc.paper.datacomponent.item.SwingAnimation.Animation,
        val duration: @Serializable(DurationSerializer::class) Duration
    ) : DataType {

        constructor(swingAnimation: io.papermc.paper.datacomponent.item.SwingAnimation) : this(swingAnimation.type(), swingAnimation.duration().ticks)

        override fun setDataType(itemStack: ItemStack) {
            val swingAnimation = io.papermc.paper.datacomponent.item.SwingAnimation.swingAnimation()
                .type(type).duration(duration.ticks).build()
            itemStack.setData(DataComponentTypes.SWING_ANIMATION, swingAnimation)
        }
    }

    @Serializable
    data class UseEffects(
        val canSprint: Boolean = false,
        val speedMultiplier: Float = 0.2f,
        val interactVibrations: Boolean = true
    ) : DataType {

        constructor(useEffects: io.papermc.paper.datacomponent.item.UseEffects) : this(
            useEffects.canSprint(), useEffects.speedMultiplier(), useEffects.interactVibrations()
        )

        override fun setDataType(itemStack: ItemStack) {
            val useEffects = io.papermc.paper.datacomponent.item.UseEffects.useEffects()
                .canSprint(canSprint).speedMultiplier(speedMultiplier).interactVibrations(interactVibrations).build()
            itemStack.setData(DataComponentTypes.USE_EFFECTS, useEffects)
        }
    }

    @Serializable
    data class PotDecorations(
        val backItem: @Serializable(KeySerializer::class) Key? = null,
        val frontItem: @Serializable(KeySerializer::class) Key? = null,
        val leftItem: @Serializable(KeySerializer::class) Key? = null,
        val rightItem: @Serializable(KeySerializer::class) Key? = null,
    ) : DataType {
        constructor(potDecorations: io.papermc.paper.datacomponent.item.PotDecorations) :
                this(potDecorations.back()?.key(), potDecorations.front()?.key(), potDecorations.left()?.key(), potDecorations.right()?.key())

        override fun setDataType(itemStack: ItemStack) {
            val (back, front) = backItem?.let(Registry.ITEM::get) to frontItem?.let(Registry.ITEM::get)
            val (left, right) = leftItem?.let(Registry.ITEM::get) to rightItem?.let(Registry.ITEM::get)
            itemStack.setData(DataComponentTypes.POT_DECORATIONS, io.papermc.paper.datacomponent.item.PotDecorations.potDecorations(back, left, right, front))
        }
    }

    @Serializable
    @JvmInline
    value class MapId(val mapId: Int) : DataType {
        constructor(mapId: io.papermc.paper.datacomponent.item.MapId) : this(mapId.id())

        override fun setDataType(itemStack: ItemStack) {
            itemStack.setData(DataComponentTypes.MAP_ID, io.papermc.paper.datacomponent.item.MapId.mapId(mapId))
        }
    }

    @Serializable
    object IntangibleProjectile

    @Serializable
    object Glider

    @Serializable
    object Unbreakable

}

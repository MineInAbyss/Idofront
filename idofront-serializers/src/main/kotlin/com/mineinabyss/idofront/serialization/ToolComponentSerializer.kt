package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.messaging.idofrontLogger
import com.mineinabyss.idofront.serialization.ToolComponentSurrogate.Rule.Companion.toToolRules
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.key.Key
import net.kyori.adventure.util.TriState
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Tag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.components.ToolComponent
import org.bukkit.inventory.meta.components.ToolComponent.ToolRule

@Serializable
@SerialName("ToolComponent")
private class ToolComponentSurrogate(
    val rules: List<Rule> = emptyList(),
    val defaultMiningSpeed: Float,
    val damagePerBlock: Int
) {

    @Serializable
    data class Rule(
        val blockTypes: List<@Serializable(KeySerializer::class) Key>,
        val speed: Float? = null,
        val correctForDrops: TriState
    ) {
        constructor(rule: ToolRule) : this(rule.blocks.map(Material::key), rule.speed, TriState.byBoolean(rule.isCorrectForDrops))

        companion object {
            fun List<Rule>.toToolRules(): List<ToolRule> {
                return map { rule ->
                    val nonLegacyMaterials = Material.entries.filterNot(Material::isLegacy)
                    val materials = rule.blockTypes.map { rule -> nonLegacyMaterials.filter { it.key() == rule.key() } }.flatten()
                    val tags = rule.blockTypes.mapNotNull { rule -> runCatching { Bukkit.getTag(Tag.REGISTRY_BLOCKS, NamespacedKey.fromString(rule.key().asString())!!, Material::class.java) }.getOrNull() }

                    if (materials.isEmpty() && tags.isEmpty()) idofrontLogger.w("Failed to find tag for ${rule}, skipping...")

                    tags.map { ItemStack.of(Material.PAPER).itemMeta.tool.addRule(it, rule.speed, rule.correctForDrops.toBoolean()) }
                        .plus(ItemStack.of(Material.PAPER).itemMeta.tool.addRule(materials, rule.speed, rule.correctForDrops.toBoolean()))
                }.flatten()
            }
        }

    }
}

object ToolComponentSerializer : KSerializer<ToolComponent> {
    override val descriptor: SerialDescriptor = ToolComponentSurrogate.serializer().descriptor
    override fun serialize(encoder: Encoder, value: ToolComponent) {
        val surrogate = ToolComponentSurrogate(value.rules.map(ToolComponentSurrogate::Rule), value.defaultMiningSpeed, value.damagePerBlock)
        encoder.encodeSerializableValue(ToolComponentSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): ToolComponent {
        return ItemStack(Material.PAPER).itemMeta.tool.apply {
            val surrogate = decoder.decodeSerializableValue(ToolComponentSurrogate.serializer())
            rules = surrogate.rules.toToolRules()
            defaultMiningSpeed = surrogate.defaultMiningSpeed
            damagePerBlock = surrogate.damagePerBlock
        }
    }
}
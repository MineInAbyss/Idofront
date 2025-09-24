package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.serialization.helpers.RegistrySerializer
import com.mineinabyss.idofront.util.toMCKey
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Registry
import org.bukkit.attribute.Attribute
import org.bukkit.enchantments.Enchantment

@Serializable
data class SerializableEnchantment(
    val enchant: @Serializable(with = EnchantmentSerializer::class) Enchantment,
    val level: Int = 1,
) {
    constructor(itemEnchantment: Map.Entry<Enchantment, Int>) : this(itemEnchantment.key, itemEnchantment.value)
    init {
        require(level > 0) { "Level must be atleast 1" }
    }
}

@JvmInline
@Serializable
@SerialName("Enchantment")
private value class EnchantmentSurrogate(val enchant: String) {
    init {
        val registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)
        require(registry.get(enchant.toMCKey()) != null)
        { "Enchantment must be valid. Valid ones are ${registry.map { it.key }}" }
    }
}

object EnchantmentSerializer: RegistrySerializer<Enchantment>(
    "com.mineinabyss.Enchantment",
    RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT),
)

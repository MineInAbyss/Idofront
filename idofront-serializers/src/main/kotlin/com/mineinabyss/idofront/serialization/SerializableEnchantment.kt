package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.serialization.helpers.RegistrySerializer
import com.mineinabyss.idofront.serialization.helpers.typedKeyNames
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.keys.EnchantmentKeys
import kotlinx.serialization.Serializable
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

object EnchantmentSerializer : RegistrySerializer<Enchantment>(
    "com.mineinabyss.Enchantment",
    { RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT) },
    { typedKeyNames(EnchantmentKeys::class.java) },
)

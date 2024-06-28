package com.mineinabyss.idofront.serialization

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
import org.bukkit.enchantments.Enchantment

@Serializable
data class SerializableEnchantment(
    val enchant: @Serializable(with = EnchantmentSerializer::class) Enchantment,
    val level: Int = 1,
) {
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

object EnchantmentSerializer : KSerializer<Enchantment> {
    override val descriptor: SerialDescriptor = EnchantmentSurrogate.serializer().descriptor
    override fun serialize(encoder: Encoder, value: Enchantment) {
        val surrogate = EnchantmentSurrogate(value.key.asString())
        encoder.encodeSerializableValue(EnchantmentSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): Enchantment {
        val surrogate = decoder.decodeSerializableValue(EnchantmentSurrogate.serializer())
        return RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(surrogate.enchant.toMCKey())!!
    }
}

package com.mineinabyss.idofront.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.NamespacedKey
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

@Serializable
@SerialName("Enchantment")
private class EnchantmentSurrogate(
    val enchant: String
) {
    init {
        require(Enchantment.getByKey(NamespacedKey.fromString(enchant)) != null) { "Enchantment must be valid" }
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
        return Enchantment.getByKey(NamespacedKey.fromString(surrogate.enchant))!!
    }
}

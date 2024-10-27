package com.mineinabyss.idofront.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.key.Key
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.components.EquippableComponent
import org.bukkit.inventory.meta.components.UseCooldownComponent

@Serializable
@SerialName("UseCooldownComponent")
private class UseCooldownComponentSurrogate(
    val seconds: Float,
    val group: @Serializable(KeySerializer::class) NamespacedKey? = null,
)

object UseCooldownComponentSerializer : KSerializer<UseCooldownComponent> {
    override val descriptor: SerialDescriptor = UseCooldownComponentSurrogate.serializer().descriptor
    override fun serialize(encoder: Encoder, value: UseCooldownComponent) {
        val surrogate = UseCooldownComponentSurrogate(value.cooldownSeconds, value.cooldownGroup)
        encoder.encodeSerializableValue(UseCooldownComponentSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): UseCooldownComponent {
        return ItemStack(Material.PAPER).itemMeta.useCooldown.apply {
            val surrogate = decoder.decodeSerializableValue(UseCooldownComponentSurrogate.serializer())
            cooldownSeconds = surrogate.seconds
            cooldownGroup = surrogate.group
        }
    }
}
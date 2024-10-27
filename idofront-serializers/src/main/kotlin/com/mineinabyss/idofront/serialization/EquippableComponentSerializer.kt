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
import org.bukkit.entity.EntityType
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.components.EquippableComponent
import org.bukkit.inventory.meta.components.FoodComponent

@Serializable
@SerialName("EquippableComponent")
private class EquippableComponentSurrogate(
    val slot: EquipmentSlot,
    val model: @Serializable(KeySerializer::class) Key? = null,
    val equipSound: Sound? = null,
    val cameraOverlay: @Serializable(KeySerializer::class) Key? = null,
    val isDamageOnHurt: Boolean = true,
    val isDispensable: Boolean = true,
    val isSwappable: Boolean = true,
    val allowedEntities: Set<EntityType>? = null,
)

object EquippableComponentSerializer : KSerializer<EquippableComponent> {
    override val descriptor: SerialDescriptor = EquippableComponentSurrogate.serializer().descriptor
    override fun serialize(encoder: Encoder, value: EquippableComponent) {
        val surrogate = EquippableComponentSurrogate(
            value.slot, value.model, value.equipSound, value.cameraOverlay,
            value.isDamageOnHurt, value.isDispensable, value.isSwappable, value.allowedEntities?.toMutableSet()
        )
        encoder.encodeSerializableValue(EquippableComponentSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): EquippableComponent {
        return ItemStack(Material.PAPER).itemMeta.equippable.apply {
            val surrogate = decoder.decodeSerializableValue(EquippableComponentSurrogate.serializer())
            slot = surrogate.slot
            model = surrogate.model?.asString()?.let(NamespacedKey::fromString)
            equipSound = surrogate.equipSound
            cameraOverlay = surrogate.cameraOverlay?.asString()?.let(NamespacedKey::fromString)
            isDamageOnHurt = surrogate.isDamageOnHurt
            isDispensable = surrogate.isDispensable
            isSwappable = surrogate.isSwappable
            allowedEntities = surrogate.allowedEntities
        }
    }
}
package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.items.editItemMeta
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

@Serializable
@SerialName("ItemStack")
private class ItemStackSurrogate(
    val type: Material,
    val amount: Int,
    val meta: ItemMeta,
)

object ItemStackSerializer : KSerializer<ItemStack> {
    override val descriptor: SerialDescriptor = ItemStackSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: ItemStack) {
        encoder.encodeSerializableValue(ItemStackSurrogate.serializer(), ItemStackSurrogate(value.type, value.amount, value.itemMeta))
    }

    override fun deserialize(decoder: Decoder): ItemStack {
        val surrogate = decoder.decodeSerializableValue(ItemStackSurrogate.serializer())
        return ItemStack(surrogate.type, surrogate.amount).editItemMeta { surrogate.meta }
    }
}

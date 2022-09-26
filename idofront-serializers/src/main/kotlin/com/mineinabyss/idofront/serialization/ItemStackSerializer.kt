package com.mineinabyss.idofront.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.configuration.MemorySection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack

class ItemStackSerializer : KSerializer<ItemStack> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("itemstack", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ItemStack {

        return ItemStack.deserialize((YamlConfiguration().apply {
            loadFromString(decoder.decodeString())
        }.get("i") as MemorySection).getValues(true)) //TODO try to encode this at the root level
    }

    override fun serialize(encoder: Encoder, value: ItemStack) {
        encoder.encodeString(YamlConfiguration().apply {
            set("i", value.serialize())
        }.saveToString())
    }
}
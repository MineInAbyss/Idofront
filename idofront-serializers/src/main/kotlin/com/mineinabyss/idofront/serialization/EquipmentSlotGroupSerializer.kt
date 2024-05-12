package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.messaging.idofrontLogger
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.EquipmentSlotGroup.*

object EquipmentSlotGroupSerializer : KSerializer<EquipmentSlotGroup> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("EquipmentSlotGroup", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: EquipmentSlotGroup) =
        encoder.encodeString(value.toString())

    override fun deserialize(decoder: Decoder): EquipmentSlotGroup =
        EquipmentSlotGroup.getByName(decoder.decodeString()) ?: run {
            idofrontLogger.w("Not a valid EquipmentSlotGroup, defaulting to ANY...")
            idofrontLogger.w("Valid options are: " + listOf(ANY, MAINHAND, OFFHAND, HAND, FEET, LEGS, CHEST, HEAD, ARMOR).joinToString())
            ANY
        }
}
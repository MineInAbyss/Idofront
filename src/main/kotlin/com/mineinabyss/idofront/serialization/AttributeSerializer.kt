package com.mineinabyss.idofront.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.EquipmentSlot
import java.util.*

@Serializable
@SerialName("SerializableAttribute")
class SerializableAttribute (
    private val attribute: Attribute,
    private val modifier: @Serializable(with = AttributeModifierSerializer::class) AttributeModifier,
) {
    operator fun component1(): Attribute {
        return attribute
    }
    operator fun component2(): AttributeModifier {
        return modifier
    }
}

@Serializable
@SerialName("AttributeModifier")
private class AttributeModifierSurrogate(
    val uuid: @Serializable(with = UUIDSerializer::class) UUID = UUID.randomUUID(),
    val name: String,
    val amount: Double,
    val operation: AttributeModifier.Operation = AttributeModifier.Operation.ADD_NUMBER,
    val slot: EquipmentSlot? = null
) {
    init {
        require(operation in AttributeModifier.Operation.values()) { "Operation needs to be valid" }
        require(slot == null || slot in EquipmentSlot.values()) { "Slot must be a valid Equipment Slot" }
        require(name.isNotEmpty()) { "Name cannot be empty" }

    }
}

object AttributeModifierSerializer : KSerializer<AttributeModifier> {
    override val descriptor: SerialDescriptor = AttributeModifierSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: AttributeModifier) {
        encoder.encodeSerializableValue(
            AttributeModifierSurrogate.serializer(),
            AttributeModifierSurrogate(value.uniqueId, value.name, value.amount, value.operation, value.slot)
        )
    }

    override fun deserialize(decoder: Decoder): AttributeModifier {
        val surrogate = decoder.decodeSerializableValue(AttributeModifierSurrogate.serializer())
        return AttributeModifier(surrogate.uuid, surrogate.name, surrogate.amount, surrogate.operation, surrogate.slot)
    }
}

package com.mineinabyss.idofront.serialization

import io.papermc.paper.component.item.ItemAttributeModifiers
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.EncodeDefault.Mode.NEVER
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.key.Key
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.EquipmentSlotGroup
import java.util.*

@Serializable
@SerialName("SerializableAttribute")
class SerializableAttribute(
    val attribute: Attribute,
    val modifier: @Serializable(with = AttributeModifierSerializer::class) AttributeModifier,
) {

    constructor(itemAttributeModifier: ItemAttributeModifiers.Entry) : this(
        itemAttributeModifier.attribute(),
        itemAttributeModifier.modifier()
    )

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
    val key: @Serializable(KeySerializer::class) NamespacedKey,
    val amount: Double,
    val operation: AttributeModifier.Operation = AttributeModifier.Operation.ADD_NUMBER,
    @EncodeDefault(NEVER)
    val slotGroup: @Serializable(EquipmentSlotGroupSerializer::class) EquipmentSlotGroup = EquipmentSlotGroup.ANY
) {
    init {
        require(operation in AttributeModifier.Operation.entries) { "Operation needs to be valid" }
        require(key.asString().isNotEmpty()) { "Key cannot be empty" }

    }
}

object AttributeModifierSerializer : KSerializer<AttributeModifier> {
    override val descriptor: SerialDescriptor = AttributeModifierSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: AttributeModifier) {
        encoder.encodeSerializableValue(
            AttributeModifierSurrogate.serializer(),
            AttributeModifierSurrogate(
                value.key,
                value.amount,
                value.operation,
                value.slotGroup
            )
        )
    }

    override fun deserialize(decoder: Decoder): AttributeModifier {
        val surrogate = decoder.decodeSerializableValue(AttributeModifierSurrogate.serializer())
        return AttributeModifier(surrogate.key, surrogate.amount, surrogate.operation, surrogate.slotGroup)
    }
}

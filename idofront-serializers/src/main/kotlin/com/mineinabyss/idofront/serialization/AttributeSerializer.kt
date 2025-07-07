package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.textcomponents.miniMsg
import com.mineinabyss.idofront.textcomponents.serialize
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers
import io.papermc.paper.datacomponent.item.attribute.AttributeModifierDisplay
import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.set.RegistrySet
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
import org.bukkit.Registry
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

@Serializable
@SerialName("SerializableAttribute")
class SerializableAttribute(
    val attribute: Attribute,
    val modifier: @Serializable(with = AttributeModifierSerializer::class) AttributeModifier,
    val display: AttributeDisplay,
) {

    constructor(itemAttributeModifier: ItemAttributeModifiers.Entry) : this(
        itemAttributeModifier.attribute(),
        itemAttributeModifier.modifier(),
        AttributeDisplay.toSerializable(itemAttributeModifier.display())
    )

    operator fun component1(): Attribute {
        return attribute
    }

    operator fun component2(): AttributeModifier {
        return modifier
    }

    operator fun component3(): AttributeModifierDisplay {
        return display.toPaper()
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
                value.slotGroup,
            )
        )
    }

    override fun deserialize(decoder: Decoder): AttributeModifier {
        val surrogate = decoder.decodeSerializableValue(AttributeModifierSurrogate.serializer())
        return AttributeModifier(surrogate.key, surrogate.amount, surrogate.operation, surrogate.slotGroup)
    }
}

@Serializable
sealed interface AttributeDisplay {

    companion object {
        fun toSerializable(display: AttributeModifierDisplay) = when (display) {
            is AttributeModifierDisplay.Hidden -> HiddenAttributeDisplay
            is AttributeModifierDisplay.Default -> DefaultAttributeDisplay
            is AttributeModifierDisplay.OverrideText -> OverrideAttributeDisplay(display.text().serialize())
            else -> DefaultAttributeDisplay
        } as AttributeDisplay
    }

    fun toPaper() : AttributeModifierDisplay {
        return when (this) {
            is HiddenAttributeDisplay -> AttributeModifierDisplay.hidden()
            is DefaultAttributeDisplay -> AttributeModifierDisplay.reset()
            is OverrideAttributeDisplay -> AttributeModifierDisplay.override(this.text.miniMsg())
        }
    }

    @Serializable
    @SerialName("HIDDEN")
    class HiddenAttributeDisplay : AttributeDisplay

    @Serializable
    @SerialName("DEFAULT")
    class DefaultAttributeDisplay : AttributeDisplay

    @Serializable
    @SerialName("OVERRIDE")
    data class OverrideAttributeDisplay(val text: String) : AttributeDisplay

}

package com.mineinabyss.idofront.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.Tag
import org.bukkit.damage.DamageType
import org.bukkit.tag.DamageTypeTags
import java.util.*

object DamageResistantSerializer : KSerializer<Tag<DamageType>> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("DamageType", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Tag<DamageType>) = encoder.encodeString(value.key().asString())

    override fun deserialize(decoder: Decoder): Tag<DamageType> =
        Bukkit.getTag(DamageTypeTags.REGISTRY_DAMAGE_TYPES, NamespacedKey.fromString(decoder.decodeString())!!, DamageType::class.java)!!
}
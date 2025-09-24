package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.serialization.helpers.RegistrySerializer
import com.mineinabyss.idofront.util.toMCKey
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.potion.PotionType

object PotionTypeSerializer : RegistrySerializer<PotionType>("com.mineinabyss.PotionType", Registry.POTION)
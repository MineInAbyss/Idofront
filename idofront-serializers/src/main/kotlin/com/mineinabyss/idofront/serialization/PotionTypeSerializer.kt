package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.serialization.helpers.RegistrySerializer
import org.bukkit.Registry
import org.bukkit.potion.PotionType

object PotionTypeSerializer : RegistrySerializer<PotionType>("com.mineinabyss.PotionType", Registry.POTION)

package com.mineinabyss.idofront.serialization

import com.mineinabyss.idofront.serialization.helpers.RegistrySerializer
import com.mineinabyss.idofront.serialization.helpers.typedKeyNames
import io.papermc.paper.registry.keys.MobEffectKeys
import org.bukkit.Registry
import org.bukkit.potion.PotionEffectType

object PotionEffectTypeSerializer : RegistrySerializer<PotionEffectType>(
    "com.mineinabyss.PotionEffectType",
    { Registry.POTION_EFFECT_TYPE },
    { typedKeyNames(MobEffectKeys::class.java) },
)

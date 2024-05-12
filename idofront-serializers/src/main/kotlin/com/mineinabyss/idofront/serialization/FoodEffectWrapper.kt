package com.mineinabyss.idofront.serialization

import kotlinx.serialization.Serializable
import org.bukkit.potion.PotionEffect

@Serializable
class FoodEffectWrapper(val effect: @Serializable(PotionEffectSerializer::class) PotionEffect, val probability: Float = 1.0f)
package com.mineinabyss.idofront.spawning

import org.bukkit.Location
import org.bukkit.entity.Entity

inline fun <reified T : Entity> Location.spawn(): T? = world?.spawn(this, T::class.java)

inline fun <reified T : Entity> Location.spawn(init: T.() -> Unit): T? = spawn<T>()?.apply { init() }

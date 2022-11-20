package com.mineinabyss.idofront.spawning

import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType

inline fun <reified T : Entity> Location.spawn(): T? = world?.spawn(this, T::class.java)

inline fun <reified T : Entity> Location.spawn(noinline init: T.() -> Unit): T? = world?.spawn(this, T::class.java, init)

fun Location.spawn(type: EntityType): Entity? = world?.spawnEntity(this, type)

fun Location.spawn(type: EntityType, init: Entity.() -> Unit): Entity? = spawn(type)?.apply { init() }


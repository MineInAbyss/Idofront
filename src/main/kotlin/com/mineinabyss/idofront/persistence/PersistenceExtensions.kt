package com.mineinabyss.idofront.persistence

import org.bukkit.entity.Entity
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin

inline fun <reified T : Any> persistent(entity: Entity, plugin: Plugin): PersistDelegateBuilder<T> =
        PersistDelegateInfo(entity.persistentDataContainer, plugin).persistent()

inline fun <reified T : Any> persistent(through: PersistentDataContainer, plugin: Plugin): PersistDelegateBuilder<T> =
        PersistDelegateInfo(through, plugin).persistent()

inline fun <reified T : Any> PersistentComponent.persistent(): PersistDelegateBuilder<T> =
        persistDelegateInfo.persistent()

inline fun <reified T : Any> PersistDelegateInfo.persistent(): PersistDelegateBuilder<T> {
    val type = when (T::class) {
        Int::class -> PersistentDataType.INTEGER
        String::class -> PersistentDataType.STRING
        else -> error("")
    }

    return PersistDelegateBuilder(type as PersistentDataType<T, T>, this)
}

infix fun <T : Any> PersistDelegateBuilder<T>.defaultTo(default: T): PersistDelegateBuilder<T> {
    initValue = default
    return this
}
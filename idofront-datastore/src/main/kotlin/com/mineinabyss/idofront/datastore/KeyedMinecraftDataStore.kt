package com.mineinabyss.idofront.datastore

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import me.dvyy.sqlite.Transaction
import me.dvyy.sqlite.WriteTransaction
import me.dvyy.sqlite.datastore.DataStore
import me.dvyy.sqlite.datastore.KeyedDataStore
import me.dvyy.sqlite.datastore.KeyedJsonTable
import org.bukkit.entity.Entity
import kotlin.uuid.toKotlinUuid

open class KeyedMinecraftDataStore<K, T>(
    table: KeyedJsonTable,
    keySerializer: KSerializer<K>,
    serializer: KSerializer<T>,
    json: Json = DataStore.defaultJson,
) : KeyedDataStore<K, T>(table, keySerializer, serializer, json) {
    context(tx: Transaction)
    operator fun get(entity: Entity, key: K): T? {
        return get(entity.uniqueId.toKotlinUuid(), key)
    }

    context(tx: Transaction)
    fun contains(entity: Entity, key: K): Boolean {
        return contains(entity.uniqueId.toKotlinUuid(), key)
    }

    context(tx: WriteTransaction)
    operator fun set(entity: Entity, key: K, value: T) {
        set(entity.uniqueId.toKotlinUuid(), key, value)
    }
}
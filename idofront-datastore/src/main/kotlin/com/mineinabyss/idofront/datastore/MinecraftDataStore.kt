package com.mineinabyss.idofront.datastore

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import me.dvyy.sqlite.Transaction
import me.dvyy.sqlite.WriteTransaction
import me.dvyy.sqlite.datastore.DataStore
import me.dvyy.sqlite.datastore.JsonTable
import org.bukkit.entity.Entity
import kotlin.uuid.toKotlinUuid

open class MinecraftDataStore<T>(
    table: JsonTable,
    serializer: KSerializer<T>,
    json: Json = Companion.defaultJson,
) : DataStore<T>(table, serializer, json) {
    context(tx: Transaction)
    operator fun get(entity: Entity): T? {
        return get(entity.uniqueId.toKotlinUuid())
    }

    context(tx: Transaction)
    operator fun contains(entity: Entity): Boolean {
        return contains(entity.uniqueId.toKotlinUuid())
    }

    context(tx: WriteTransaction)
    operator fun set(entity: Entity, value: T) {
        set(entity.uniqueId.toKotlinUuid(), value)
    }
}
package com.mineinabyss.idofront.datastore

import com.github.shynixn.mccoroutine.bukkit.scope
import com.mineinabyss.idofront.Idofront
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.KSerializer
import me.dvyy.sqlite.Database
import me.dvyy.sqlite.Transaction
import me.dvyy.sqlite.WriteTransaction
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

interface DataStoreLike {
    context(ctx: WriteTransaction)
    fun init()

}

open class KeyedDataStore<K, T>(
    val tableName: String,
    val keySerializer: KSerializer<K>,
    val serializer: KSerializer<T>,
) : DataStoreLike {
    context(ctx: WriteTransaction)
    override fun init() {
        TODO()
    }

    context(ctx: Transaction)
    operator fun get(entity: Entity, key: K): T {
        TODO()
    }

    context(ctx: Transaction)
    operator fun set(entity: Entity, key: K, value: T?) {
        TODO()
    }
}

open class DataStore<T>(
    val tableName: String,
    val serializer: KSerializer<T>,
) : DataStoreLike {
    context(ctx: WriteTransaction)
    override fun init() {
        TODO()
    }


    context(ctx: Transaction)
    operator fun get(uuid: UUID): T {
        TODO()
    }

    context(ctx: Transaction)
    operator fun get(entity: Entity): T {
        TODO()
    }

    context(ctx: WriteTransaction)
    operator fun set(uuid: UUID, value: T?) {
        TODO()
    }

    context(ctx: WriteTransaction)
    operator fun set(entity: Entity, value: T?) {
        TODO()
    }

    context(ctx: WriteTransaction)
    fun update(entity: Entity, block: T.() -> T) {

    }


    context(ctx: WriteTransaction)
    fun update(uuid: UUID, block: T.() -> Unit) {

    }

}

fun Idofront.setupDataStore(store: DataStoreLike) {
    playerDatabase.launchWrite { store.init() }
}

val playerDatabase = Database.temporary()

inline fun <T> Player.readBlocking(crossinline block: context(Transaction) () -> T): T {
    return playerDatabase.read { block() }
}

suspend inline fun <T> Player.read(crossinline block: context(Transaction) () -> T): T {
    return playerDatabase.read { block() }
}

suspend inline fun <T> Player.write(crossinline block: context(WriteTransaction) () -> T): T {
    return playerDatabase.write { block() }
}


inline fun <T> Player.launchRead(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    crossinline block: context(Transaction) () -> T,
): Job {
    return Idofront.plugin.scope.launch(context, start) {
        playerDatabase.read { block() }
    }
}


inline fun <T> Player.launchWrite(crossinline block: context(WriteTransaction) () -> T): Job {
    return playerDatabase.launchWrite { block() }
}

package com.mineinabyss.idofront.datastore

import com.github.shynixn.mccoroutine.bukkit.scope
import com.mineinabyss.idofront.Idofront
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.dvyy.sqlite.Transaction
import me.dvyy.sqlite.WriteTransaction
import org.bukkit.entity.Player
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.io.path.Path

val Idofront.db by lazy { Idofront.sqliteDatabase(Idofront.plugin, Path("player_data.db")) }

suspend inline fun <T> Player.read(crossinline block: context(Transaction) () -> T): T {
    return Idofront.db.read { block() }
}

suspend inline fun <T> Player.write(crossinline block: context(WriteTransaction) () -> T): T {
    return Idofront.db.write { block() }
}

inline fun <T> Player.readBlocking(crossinline block: context(Transaction) () -> T): T {
    return Idofront.db.readBlocking { block() }
}

inline fun <T> Player.launchRead(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    crossinline block: context(Transaction) () -> T,
): Job {
    return Idofront.plugin.scope.launch(context, start) {
        Idofront.db.read { block() }
    }
}

inline fun <T> Player.launchWrite(crossinline block: context(WriteTransaction) () -> T): Job {
    return Idofront.db.launchWrite { block() }
}

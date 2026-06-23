package com.mineinabyss.idofront.datastore

import com.github.shynixn.mccoroutine.bukkit.scope
import com.mineinabyss.idofront.Idofront
import me.dvyy.sqlite.Database
import me.dvyy.sqlite.Transaction
import me.dvyy.sqlite.WriteTransaction
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.div


/**
 * Runs a read transaction while blocking the current thread.
 * Useful when a database read must be performed when:
 * - An event listener wants to modify the fired event.
 * - A player connection is being established.
 * - A packet is being manipulated.
 *
 * Can only run on the primary server thread, use suspending api in other contexts.
 */
fun <T> Database.readBlocking(block: Transaction.() -> T): T {
    if (!Bukkit.isPrimaryThread()) {
        error("Cannot block database read from non-tick thread, use non-blocking api instead. Current thread was: ${Thread.currentThread()}")
    }

    val conn = getOrCreateReadConnectionForCurrentThread()
    return Transaction(conn, defaultIdentity ?: error("Default identity must be provided")).block()
}

/**
 * Creates a new sqlite database in WAL mode at a [path] relative to this plugin's data path.
 * Database connections will automatically be closed when the plugin is disabled.
 */
fun Idofront.sqliteDatabase(plugin: Plugin, path: Path, init: WriteTransaction.() -> Unit = {}): Database {
    val dbPath = (plugin.dataPath / path).absolutePathString()
    //TODO a thread pool for the whole plugin, not each db?
    return Database(dbPath, init = init, parentScope = plugin.scope)
}
package com.mineinabyss.idofront.plugin

import com.mineinabyss.idofront.messaging.ComponentLogger

/**
 * Provides useful functions for load and unload logic.
 */
class ActionScope(val logger: ComponentLogger) {
    /** @see attempt */
    inline operator fun <T> String.invoke(block: AttemptBlock.() -> T) =
        attempt(this, this, block = block)

    class AttemptBlock(val scope: ActionScope, val msg: String, val level: Int) {
        var printed = false
        inline operator fun <T> String.invoke(block: AttemptBlock.() -> T): Result<T> {
            if (!printed) {
                scope.logger.iSuccess(msg)
                printed = true
            }
            return scope.attempt(this, this, level + 1, block)
        }
    }

    fun String.addIndent(level: Int) = buildString {
        repeat(level * 2) { append(' ') }
        append(this@addIndent)
    }

    /** Uses [runCatching] to print a success and failure message to the sender.
     *
     * Will not throw any error, mark as [! important][not] for this. */
    inline fun <T> attempt(
        success: String,
        fail: String = success,
        level: Int = 0,
        block: AttemptBlock.() -> T
    ): Result<T> {
        val attempt = AttemptBlock(this, success, level)
        return runCatching { attempt.block() }
            .onSuccess {
                if (attempt.printed) return@onSuccess
                logger.iSuccess(success.addIndent(level))
            }
            .onFailure {
                if (attempt.printed) return@onFailure
                logger.iFail(fail.addIndent(level))
                if (level == 0)
                    it.printStackTrace()
            }
    }

    /** Marks an attempt as important and will throw an error if it fails, ex:
     *
     * `!"I will make an exception on fail" { ... }`
     * @see Result.getOrThrow */
    operator fun <T> Result<T>.not() = getOrThrow()
}

fun actions(logger: ComponentLogger, run: ActionScope.() -> Unit) {
    ActionScope(logger).apply(run)
}

@Deprecated("Pass a logger!", ReplaceWith("actions(logger)"))
fun actions(run: ActionScope.() -> Unit) {
    ActionScope(ComponentLogger.fallback()).apply(run)
}

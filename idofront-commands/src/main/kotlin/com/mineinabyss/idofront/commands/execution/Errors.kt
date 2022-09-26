package com.mineinabyss.idofront.commands.execution

import com.mineinabyss.idofront.commands.BaseCommand
import com.mineinabyss.idofront.messaging.error

/**
 * Stops a command from executing further by throwing a [CommandExecutionFailedException].
 *
 * @param message An error message to be sent to the sent to the sender of the command. Nothing is sent if null.
 * @param onFail Additional actions to take upon failure. Will execute after the message is sent.
 */
inline fun <T : BaseCommand> T.stopCommand(message: String? = null, onFail: (T.() -> Unit) = {}): Nothing {
    if (message != null) sender.error(message)
    onFail()
    throw CommandExecutionFailedException()
}

/**
 * Thrown when a command has failed to execute for any reason.
 *
 * It is used to stop the command from executing further in the DSL.
 */
class CommandExecutionFailedException : Exception()

package com.mineinabyss.idofront.commands.conditions

import com.mineinabyss.idofront.commands.Command
import com.mineinabyss.idofront.commands.CommandExtension
import com.mineinabyss.idofront.commands.ConditionLambda
import com.mineinabyss.idofront.commands.ExecutableCommand
import com.mineinabyss.idofront.messaging.error


//CONDITIONS
/**
 * @property check The check to run on execution. If returns true, the command can proceed, if false, [fail] will
 * be called.
 */
class Condition(val check: Command.() -> Boolean) {
    private val runOnFail = mutableListOf<CommandExtension>()

    fun orElse(run: CommandExtension) = runOnFail.add(run).let { this }

    fun orElseError(error: String) = runOnFail.add { sender.error(error) }.let { this }

    internal fun fail(execution: ExecutableCommand) = runOnFail.forEach { it.invoke(execution) }
}
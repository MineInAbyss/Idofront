package com.mineinabyss.idofront.commands.arguments

import com.mineinabyss.idofront.commands.Command
import com.mineinabyss.idofront.commands.GenericCommand

open class IntArg(name: String, init: CmdInit<IntArg>? = null) : CommandArgument<Int>(name, init.cast()) {
    init {
        parseErrorMessage = { "$it is not a valid integer for the $name" }
    }

    override fun parse(command: GenericCommand): Int = command.arg.toInt()

    override fun verify(execution: Command.Execution): Boolean =
            when {
                execution.arg.toIntOrNull() == null -> {
                    execution.sendParseError()
                    false
                }
                else -> true
            }
}
package com.mineinabyss.idofront.commands.arguments

import com.mineinabyss.idofront.commands.Command
import com.mineinabyss.idofront.commands.GenericCommand

open class BooleanArg(name: String, init: CmdInit<BooleanArg>? = null) : CommandArgument<Boolean>(name, init.cast()) {
    init {
        parseErrorMessage = { "$name can only be true or false, not $it" }
        missingMessage = "Please input whether $name is true or false"
    }

    override fun parse(command: GenericCommand): Boolean = command.arg.toBoolean()

    override fun verify(execution: Command.Execution) =
            when (execution.arg) {
                "true", "false" -> true
                else -> {
                    execution.sendParseError()
                    false
                }
            }
}
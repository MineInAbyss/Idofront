package com.mineinabyss.idofront.commands.arguments

import com.mineinabyss.idofront.commands.GenericCommand

open class StringArg(name: String, init: CmdInit<StringArg>? = null) : CommandArgument<String>(name, init.cast()) {
    override fun parse(command: GenericCommand): String =
            command.arg


    override fun verify(command: GenericCommand) = true
}
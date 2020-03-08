package com.mineinabyss.idofront.commands.arguments

import com.mineinabyss.idofront.commands.GenericCommand

open class CustomArg<T>(
        name: String,
        val parse: CustomArg<T>.() -> T,
        val verify: (CustomArg<T>.() -> Boolean) = {
            try {
                this.parse()
                true
            } catch (e: Exception) {
                false
            }
        },
        init: CmdInit<CustomArg<T>>? = null
) : CommandArgument<T>(name, init.cast()) {
    override fun parse(command: GenericCommand): T = parse()

    override fun verify(command: GenericCommand): Boolean {
        return if(verify())
            true
        else {
            command.sendParseError()
            false
        }

    }
}
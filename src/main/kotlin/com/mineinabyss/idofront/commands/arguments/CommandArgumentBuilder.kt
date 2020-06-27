package com.mineinabyss.idofront.commands.arguments

import com.mineinabyss.idofront.commands.ChildContainingCommand

//TODO might not need this wrapper
class CommandArgumentBuilder<T>(
        val init: (CommandArgument<T>.() -> Unit)?
)

fun <T> ChildContainingCommand.arg(
        init: (CommandArgument<T>.() -> Unit)? = null
) = CommandArgumentBuilder(init)
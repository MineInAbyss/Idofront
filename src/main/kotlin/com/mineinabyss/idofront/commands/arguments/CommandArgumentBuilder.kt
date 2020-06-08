package com.mineinabyss.idofront.commands.arguments

import com.mineinabyss.idofront.commands.Containable

//TODO might not need this wrapper
class CommandArgumentBuilder<T>(
        val init: (CommandArgument<T>.() -> Unit)?
)

fun <T> Containable.arg(
        init: (CommandArgument<T>.() -> Unit)? = null
) = CommandArgumentBuilder(init)
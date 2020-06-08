package com.mineinabyss.idofront.commands.arguments

import com.mineinabyss.idofront.commands.GenericCommand

/**
 * Holds a list of [strings] that were passed by the user when executing a command, and a list of [arguments] that
 * should be associated with those strings.
 */
class ArgumentParser(
        val strings: List<String>,
        private val arguments: MutableList<CommandArgument<*>> = mutableListOf()
) {
    val size get() = strings.size
    val argumentsSize get() = arguments.size
    val argumentNames get() = arguments.joinToString { "<${it.name}>" }

    //TODO args store a reference to the command they were originally in, instead of using the class accessing them.
    // That lets us access them from function literals with recievers, i.e. Command.() -> Unit, but we need to handle
    // how args are accessed from child commands.
    fun childParser() = ArgumentParser(strings.drop(1), arguments.toMutableList())

    fun addArgument(argument: CommandArgument<*>) {
        arguments += argument
    }

    fun verifyArgumentsFor(command: GenericCommand): Boolean =
            strings.size <= arguments.size && command.run { arguments.all { it.verifyAndCheckMissing(command) } }

    operator fun get(commandArgument: CommandArgument<*>): String = strings[arguments.indexOf(commandArgument)]
}
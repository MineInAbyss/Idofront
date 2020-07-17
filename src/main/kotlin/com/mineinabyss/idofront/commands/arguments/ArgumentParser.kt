package com.mineinabyss.idofront.commands.arguments

import com.mineinabyss.idofront.commands.BaseCommand

/**
 * Holds a list of [strings] that were passed by the user when executing a command, and a list of [arguments] that
 * should be associated with those strings.
 *
 * @param strings The strings passed when running the command.
 * @param arguments The arguments registered for this parser with [addArgument].
 */
class ArgumentParser(
        strings: List<String>,
        arguments: Collection<CommandArgument<*>> = setOf()
) : Argumentable {
    override val strings = strings.toList()
    override val arguments get() = _arguments.toSet()
    private val _arguments = arguments.toMutableSet()

    override fun addArgument(argument: CommandArgument<*>) {
        _arguments += argument
    }

    override fun argumentsMetFor(command: BaseCommand): Boolean =
            strings.size <= _arguments.size && command.run { _arguments.all { it.verifyAndCheckMissing(command) } }

    override operator fun get(commandArgument: CommandArgument<*>): String = strings[_arguments.indexOf(commandArgument)]
}
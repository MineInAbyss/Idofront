package com.mineinabyss.idofront.commands.arguments

import com.mineinabyss.idofront.commands.BaseCommand

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
           command.run { _arguments.all { it.verifyAndCheckMissing(command) } }

    override operator fun get(commandArgument: CommandArgument<*>): String = strings[_arguments.indexOf(commandArgument)]
}
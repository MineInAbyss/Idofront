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

    fun childParser() = ArgumentParser(strings, arguments.toMutableList())

    fun addArgument(argument: CommandArgument<*>) {
        arguments += argument
    }

    fun verifyArgumentsFor(command: GenericCommand): Boolean = arguments.all { it.verifyAndCheckMissing(command) }

    operator fun get(depth: Int, pos: Int): String = strings[depth + pos]
}
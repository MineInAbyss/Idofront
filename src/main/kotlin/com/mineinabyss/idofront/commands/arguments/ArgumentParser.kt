package com.mineinabyss.idofront.commands.arguments

import com.mineinabyss.idofront.commands.GenericCommand

class ArgumentParser(
        val args: List<String>,
        private val arguments: MutableList<CommandArgument<*>> = mutableListOf()
) {
    val size get() = args.size

    fun childParser() = ArgumentParser(args, arguments.toMutableList())

    fun addArgument(argument: CommandArgument<*>) {
        arguments += argument
        argument.order = arguments.size - 1
    }

    fun verifyArgumentsFor(command: GenericCommand): Boolean = arguments.all { it.verifyAndCheckMissing(command)}

    operator fun get(depth: Int, pos: Int): String = args[depth + pos]
}
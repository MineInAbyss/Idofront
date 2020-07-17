package com.mineinabyss.idofront.commands.arguments

import com.mineinabyss.idofront.commands.BaseCommand
import com.mineinabyss.idofront.commands.CommandCreation
import com.mineinabyss.idofront.commands.CommandGroup

/**
 *
 * @property strings A list of strings representing arguments.
 * @property arguments Arguments to be checked.
 *
 * @property argumentsSize The amount of [CommandArgument]s registered for this parser
 * @property argumentsWereSent
 * @property argumentNames A nicely formatted list of all the arguments avaiable for this command
 * @property firstArgument The first string passed in [strings]
 */
interface Argumentable {
    val strings: List<String>
    val arguments: Set<CommandArgument<*>>

    fun addArgument(argument: CommandArgument<*>)
    fun argumentsMetFor(command: BaseCommand): Boolean

    operator fun get(commandArgument: CommandArgument<*>): String

    fun firstArgumentIsFor(subcommands: Collection<CommandCreation>): Boolean =
            argumentsWereSent && subcommands.any { it.names.contains(firstArgument) }

    /**
     * Creates a new argument parser for subcommands, droping the first passed argument.
     * Used to get the scope for arguments working correctly (otherwise a parent command could force arguments to
     * be passed, which only exit for its subcommands).
     */
    fun childParser(): ArgumentParser = ArgumentParser(strings.drop(1), arguments)

    /** Same as [childParser] but does not drop the first argument. Useful for [CommandGroup] */
    fun childGroupParser(): ArgumentParser = ArgumentParser(strings, arguments)

    val argumentsSize get() = arguments.size
    val argumentsWereSent get() = strings.isNotEmpty()
    val argumentNames
        get() = arguments.joinToString(separator = " ") {
            val succeeded = it.argumentWasPassed && it.parsedSuccessfully == true
            val prefix = if (succeeded) "" else "&c"
            val suffix = if (succeeded) " = ${it.passed}" else ""
            "&7<$prefix${it.name}$suffix&7>"
        }
    val firstArgument get() = strings[0]

    /** A sort of local typealias that can then be used by [BaseCommand.provideDelegate] to cleanly add an argument */
    fun <T> arg(init: (CommandArgument<T>.() -> Unit)? = null) = init
}

fun BaseCommand.argumentsMet() = argumentsMetFor(this)
package com.mineinabyss.idofront.commands.arguments

import com.mineinabyss.idofront.commands.BaseCommand
import com.mineinabyss.idofront.commands.CommandGroup

/**
 * Holds a list of [strings] that were passed by the user when executing a command, and a list of [arguments] that
 * should be associated with those strings.
 *
 * @property strings A list of strings passed when running the command representing arguments.
 * @property arguments Arguments to be checked.
 *
 * @property argumentsWereSent Were any arguments sent?
 * @property argumentNames A nicely formatted list of all the arguments available for this command.
 * @property firstArgument The first string passed in [strings]
 */
interface Argumentable {
    val strings: List<String>
    val arguments: Set<CommandArgument<*>> //TODO fix ambiguity between the names: arguments and strings

    val argumentsWereSent get() = strings.isNotEmpty()
    val argumentNames
        get() = arguments.joinToString(separator = " ") {
            val succeeded = it.argumentWasPassed && it.parsedSuccessfully == true
            val defaultPresent = it.default != null
            val prefix = if (succeeded) "" else if (defaultPresent) "<blue>" else "<red>"
            val suffix = if (succeeded) " = ${it.passed}" else if (defaultPresent) " = ${it.default}" else ""
            "<gray><$prefix${it.name}$suffix<gray>>"
        }
    val firstArgument get() = strings[0]

    /** Adds the passed [argument] to [arguments] */
    fun addArgument(argument: CommandArgument<*>)

    /** @return Are the requirements for the registered [arguments] met for a [command]. */
    fun argumentsMetFor(command: BaseCommand): Boolean

    /** @return The passed argument associated with a [commandArgument] */
    operator fun get(commandArgument: CommandArgument<*>): String

    /** @return Whether the first argument passed is for a subcommand */
    fun firstArgumentIsFor(subcommands: Collection<BaseCommand>): Boolean =
        argumentsWereSent && subcommands.any { it.names.contains(firstArgument) }

    /**
     * Creates a new argument parser for subcommands, droping the first passed argument.
     * Used to get the scope for arguments working correctly (otherwise a parent command could force arguments to
     * be passed, which only exit for its subcommands).
     */
    fun childParser(): ArgumentParser = ArgumentParser(strings.drop(1), arguments)

    /** Same as [childParser] but does not drop the first argument. Useful for [CommandGroup] */
    fun childGroupParser(): ArgumentParser = ArgumentParser(strings, arguments)

    /** A sort of local typealias that can then be used by [BaseCommand.provideDelegate] to cleanly add an argument */
    fun <T> arg(init: (CommandArgument<T>.() -> Unit)) = init
}

/** @see Argumentable.argumentsMetFor */
fun BaseCommand.argumentsMet() = argumentsMetFor(this)

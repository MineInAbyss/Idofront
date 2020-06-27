package com.mineinabyss.idofront.commands

/**
 * Anything in the chain of commands that's able to contain other commands. Forces implementation of a method of
 * sharing init blocks between multiple sub-commands,
 *
 * @property sharedInit A list of init blocks that should be run on all sub-commands directly under this command.
 */
abstract class ChildContainingCommand : Tag() {
    protected val sharedInit = mutableListOf<Command.() -> Unit>()

    /**
     * Will run on creation of all sub-commands in this command group. Useful for sharing conditions.
     *
     * If multiple shared blocks are created, all blocks declared above a command will be executed. Anything below
     * will not, so you can add additional conditions or run additional actions on specific commands.
     */
    fun shared(conditions: Command.() -> Unit) = sharedInit.add(conditions)

    /** Runs a child command */
    internal abstract fun runChildCommand(subcommand: CommandCreation): CommandCreation
}
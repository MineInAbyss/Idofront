package com.mineinabyss.idofront.commands

/**
 * Anything in the chain of commands that's able to contain other commands. Forces implementation of a method of
 * sharing init blocks between multiple sub-commands,
 *
 * @property sharedInit A list of init blocks that should be run on all sub-commands directly under this command.
 * @property depth The depth of this command within the hierarchy. (i.e. top commands have a depth of 0, a sub-command
 * of these will have a depth of 1)
 */
abstract class Containable : Tag() {
    protected val sharedInit = mutableListOf<Command.() -> Unit>()
    abstract val depth: Int

    /**
     * Will run on creation of all sub-commands in this command group. Useful for sharing conditions.
     *
     * If multiple shared blocks are created, all blocks declared above a command will be executed. Anything below
     * will not, so you can add additional conditions or run additional actions on specific commands.
     */
    fun shared(conditions: Command.() -> Unit) = sharedInit.add(conditions)

    /**
     * Adds a child command
     */
    abstract fun addChild(creation: CommandCreation): CommandCreation

}

fun List<Command.() -> Unit>.applyTo(command: Command) = this.forEach { command.it() }
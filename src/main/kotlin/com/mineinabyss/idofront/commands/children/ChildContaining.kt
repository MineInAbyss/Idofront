package com.mineinabyss.idofront.commands.children

import com.mineinabyss.idofront.commands.BaseCommand
import com.mineinabyss.idofront.commands.Command
import com.mineinabyss.idofront.commands.CommandCreation

//TODO use generics but don't do the `where` stuff, just have one class that is everything we need
interface ChildContaining {
    val subcommands: List<CommandCreation>
    val sharedInit: List<Command.() -> Unit>

    /**
     * Will run on creation of all sub-commands in this command group. Useful for sharing conditions.
     *
     * If multiple shared blocks are created, all blocks declared above a command will be executed. Anything below
     * will not, so you can add additional conditions or run additional actions on specific commands.
     */
    fun shared(conditions: Command.() -> Unit)

    /** Runs a child command */
    fun runChildCommandOn(command: BaseCommand, subcommand: CommandCreation): CommandCreation

}

/** Runs a child command */
fun BaseCommand.runChildCommand(subcommand: CommandCreation) = runChildCommandOn(this, subcommand)
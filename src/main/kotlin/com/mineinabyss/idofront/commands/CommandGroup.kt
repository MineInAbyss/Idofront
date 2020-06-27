package com.mineinabyss.idofront.commands

import com.mineinabyss.idofront.commands.arguments.ArgumentParser
import org.bukkit.command.CommandSender

/**
 * Command groups limit arguments defined inside of them to only the commands in the group.
 *
 * @property argumentParser A copy of the argumentParser is made so we aren't forced to pass the arguments in the group
 * for commands outside the group.
 */
class CommandGroup<T>(
        private val parent: T,
        override val sender: CommandSender,
        argumentParser: ArgumentParser
) : ExecutableCommand() where T : ChildContainingCommand,
                              T : Permissionable {
    override val argumentParser: ArgumentParser = argumentParser.childParser() //TODO might need this for all commands

    fun command(vararg names: String, desc: String = "", init: Command.() -> Unit) {
        parent.runChildCommand(CommandCreation(names.toList(), parent.parentPermission, sharedInit, desc, init, argumentParser))
    }

    override fun runChildCommand(subcommand: CommandCreation): CommandCreation {
        parent.runChildCommand(subcommand)
        return subcommand
    }
}
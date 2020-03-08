package com.mineinabyss.idofront.commands

import com.mineinabyss.idofront.commands.arguments.ArgumentParser
import org.bukkit.command.CommandSender

class CommandGroup<T>(
        val parent: T,
        override val sender: CommandSender,
        argumentParser: ArgumentParser
) : GenericCommand() where T : Containable,
                           T : Permissionable {

    //The point of command groups is to limit arguments defined inside of them to only the commands in the group.
    // as such, we must make our own copy of the argumentParser so we aren't forced to pass the arguments in the group
    // for commands outside the group.
    override val argumentParser: ArgumentParser = argumentParser.childParser() //TODO might need this for all commands
    override val depth: Int = parent.depth + 1

    fun command(vararg names: String, desc: String = "", init: Command.() -> Unit) {
        parent.addChild(CommandCreation(names.toList(), parent.permissionChain, sharedInit, desc, init, argumentParser))
    }

    override fun addChild(creation: CommandCreation): CommandCreation {
        parent.addChild(creation)
        return creation
    }
}
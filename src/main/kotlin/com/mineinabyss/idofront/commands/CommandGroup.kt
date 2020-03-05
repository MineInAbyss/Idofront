package com.mineinabyss.idofront.commands

import org.bukkit.command.CommandSender

class CommandGroup<T>(
        val parent: T,
        override val sender: CommandSender,
        override val args: List<String>
) : GenericCommand() where T : Containable,
                           T : Permissionable {

    fun command(vararg names: String, init: Command.() -> Unit) {
        sharedInit
        //TODO permission needs to be passed somehow
        val command = parent.addChild(CommandCreation(names.toList(), "", arguments, sharedInit, init)) //* is for varargs
    }

    override fun addChild(creation: CommandCreation): CommandCreation {
        parent.addChild(creation)
        return creation
    }
}
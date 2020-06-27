package com.mineinabyss.idofront.commands

import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

class CommandHolder( //TODO allow for holding arguments here
        private val plugin: JavaPlugin,
        private val commandExecutor: IdofrontCommandExecutor
) : ChildContainingCommand() {
    internal val commands = mutableListOf<CommandCreation>()

    operator fun get(commandName: String): CommandCreation? =
            commands.firstOrNull { it.names.any { name -> name == commandName } }

    fun command(vararg names: String, topPermission: String = plugin.name.toLowerCase(), init: Command.() -> Unit) {
        names.forEach {
            (plugin.getCommand(it) ?: error("Error registering command $it")).setExecutor(commandExecutor)
        }
        commands += CommandCreation(names.toList(), topPermission, sharedInit, "", init)
    }

    fun execute(creation: CommandCreation, sender: CommandSender, args: List<String>) {
        creation.newInstance(sender, args)
    }

    /**
     * Group commands which share methods or variables together, so commands outside this scope can't see them
     */
//    fun commandGroup(init: CommandGroup<Command>.() -> Unit) {
//        CommandGroup(this, sender, args).init()
//    }

    override fun runChildCommand(subcommand: CommandCreation): CommandCreation {
        commands += subcommand
        return subcommand
    }
}
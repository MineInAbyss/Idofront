package com.mineinabyss.idofront.commands

import com.mineinabyss.idofront.commands.execution.CommandExecutionFailedException
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

//TODO allow for holding arguments here. The current limitation is that only one instance of the command holder is
// ever present. It has no idea about the sender or their arguments until one of the commands is actually executed,
// and a list of top level commands needs to exist to be registered with plugin.getCommand.setExecutor.
/**
 * A class for holding a list of top level commands. One instance is created during plugin startup through
 * [IdofrontCommandExecutor.commands].
 *
 * The class itself is accessed in [IdofrontCommandExecutor.onCommand], which will find the applicable command and
 * [execute] a new instance of it with the sender and their arguments.
 */
class CommandHolder(
        private val plugin: JavaPlugin,
        private val commandExecutor: IdofrontCommandExecutor
) : ChildContainingCommand() { //command holder itself isn't exe
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
        try {
            creation.newInstance(sender, args)
        } catch (e: CommandExecutionFailedException) {
            //TODO print something here
        }
    }

    override fun runChildCommand(subcommand: CommandCreation): CommandCreation {
        commands += subcommand
        return subcommand
    }
}
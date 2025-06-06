package com.mineinabyss.idofront.commands.entrypoint

import com.mineinabyss.idofront.commands.Command
import com.mineinabyss.idofront.commands.CommandDSLElement
import com.mineinabyss.idofront.commands.arguments.ArgumentParser
import com.mineinabyss.idofront.commands.children.ChildSharing
import com.mineinabyss.idofront.commands.children.ChildSharingManager
import com.mineinabyss.idofront.commands.children.CommandCreating
import com.mineinabyss.idofront.commands.execution.CommandExecutionFailedException
import com.mineinabyss.idofront.commands.execution.IdofrontCommandExecutor
import com.mineinabyss.idofront.messaging.error
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
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
class CommandDSLEntrypoint(
    private val plugin: JavaPlugin,
    private val commandExecutor: IdofrontCommandExecutor
) : CommandDSLElement,
    ChildSharing by ChildSharingManager(),
    CommandCreating {
    private val subcommands = mutableMapOf<List<String>, MutableList<(CommandSender, List<String>) -> Command>>()

    fun execute(name: String, sender: CommandSender, args: List<String>) {
        val matchedCommands = get(name)
            ?: sender.error("Command $name not found, although registered at some point").let { return }
        try {
            matchedCommands.forEach { it(sender, args) }
        } catch (e: CommandExecutionFailedException) {
            //thrown whenever any error on the sender's part occurs, to stop running through the DSL at any point
        }
    }


    override fun command(vararg names: String, desc: String, init: Command.() -> Unit): Command? {
        val pluginName: String = plugin.name.lowercase()
        val name = names.first()
        // register command with bukkit as a top-level command
        plugin.server.commandMap.register(name, pluginName,
            object : org.bukkit.command.Command(name, desc, "/$name", names.drop(1)) {
                override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
                    return commandExecutor.onCommand(sender, this, commandLabel, args)
                }

                override fun tabComplete(
                    sender: CommandSender,
                    alias: String,
                    args: Array<out String>
                ): MutableList<String> {
                    if(commandExecutor is TabCompleter)
                        return commandExecutor.onTabComplete(sender, this, alias, args) ?: mutableListOf()
                    return mutableListOf()
                }
            })

        // Add as a subcommand
        subcommands.getOrPut(names.toList()) { mutableListOf() } += { sender, arguments ->
            Command(
                nameChain = listOf(names.first()),
                names = names.toList(),
                sender = sender,
                argumentParser = ArgumentParser(arguments),
                parentPermission = pluginName.takeIf { it !in names },
                description = desc
            ).runWith(init)
        }
        return null
    }

    operator fun get(commandName: String): List<((CommandSender, List<String>) -> Command)>? {
        for ((names, command) in subcommands) {
            if (names.contains(commandName)) return command
        }
        return null
    }
}

package com.mineinabyss.idofront.commands

import com.mineinabyss.idofront.messaging.error
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.plugin.java.JavaPlugin

abstract class IdofrontCommandExecutor : CommandExecutor, TabCompleter {
    abstract val commands: CommandHolder

    /**
     * Gets the command or send the player a message if it isn't found
     */
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        commands.execute(commands[label]
                ?: sender.error("Command $label not found, although registered at some point").let { return true },
                sender,
                args.toList())

        return true
    }

    //TODO have reference to command executor and plugin
    fun commands(plugin: JavaPlugin, init: CommandHolder.() -> Unit): CommandHolder {
        val commandHolder = CommandHolder(plugin, this)
        commandHolder.init()
        return commandHolder
    }
}
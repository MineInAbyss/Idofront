package com.mineinabyss.idofront.commands

import com.mineinabyss.idofront.messaging.error
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.plugin.java.JavaPlugin

abstract class IdofrontCommandExecutor : CommandExecutor, TabCompleter {
    abstract val commands: CommandHolder

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        //get the command or send the player a message if it isn't found
        (commands[label] ?: sender.error("Command $label not found").let { return true })
                .execute(sender, args.toList())
        return true
    }

    //TODO have reference to command executor and plugin
    fun commands(plugin: JavaPlugin, init: CommandHolder.() -> Unit): CommandHolder {
        val commands = CommandHolder(plugin, this)
        commands.init()
        return commands
    }
}
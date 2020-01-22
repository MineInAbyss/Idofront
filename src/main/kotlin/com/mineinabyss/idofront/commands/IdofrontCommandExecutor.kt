package com.mineinabyss.idofront.commands

import com.mineinabyss.idofront.error
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

internal val commandExecutor by lazy { IdofrontCommandExecutor() }

class IdofrontCommandExecutor : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        //get the command or send the player a message if it isn't found
        (commandRegisterer[label] ?: sender.error("Command $label not found").let { return true })
                .execute(sender, args.toList())
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): MutableList<String> {
        return mutableListOf()
    }
}
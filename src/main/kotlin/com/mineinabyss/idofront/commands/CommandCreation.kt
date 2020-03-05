package com.mineinabyss.idofront.commands

import com.mineinabyss.idofront.commands.arguments.CommandArgument
import org.bukkit.command.CommandSender

class CommandCreation(
        val names: List<String>,
        val topPermission: String,
        val commandArguments: List<CommandArgument<*>> = listOf(),
        val sharedInit: List<Command.() -> Unit> = listOf(),
        val init: Command.() -> Unit
){
    fun newInstance(sender: CommandSender, args: List<String>): Command{
        val command = Command(names, sender, args, topPermission)
        command.addArguments(commandArguments)
        command.applyShared()
        return command
    }
}
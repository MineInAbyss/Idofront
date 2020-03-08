package com.mineinabyss.idofront.commands

import com.mineinabyss.idofront.commands.arguments.ArgumentParser
import org.bukkit.command.CommandSender

class CommandCreation(
        val names: List<String>,
        val topPermission: String,
        val sharedInit: List<Command.() -> Unit> = listOf(),
        val description: String,
        val init: Command.() -> Unit,
        val argumentParser: ArgumentParser? = null
) {

    fun newInstance(sender: CommandSender, args: List<String>, depth: Int): Command {
        val command = Command(names, sender, argumentParser ?: ArgumentParser(args), topPermission, depth)
        sharedInit.applyTo(command)
        command.init()
        return command
    }
}
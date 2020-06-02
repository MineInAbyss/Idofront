package com.mineinabyss.idofront.commands

import com.mineinabyss.idofront.commands.arguments.ArgumentParser
import org.bukkit.command.CommandSender

/**
 * A template for creating a [Command] (through [newInstance]). We re-instantiate everything in the command hierarchy
 * to keep every instance separate from any other, but the issue was that we need to find a subcommand of a subcommand
 * by name, which is only accessible once instantiated, and running an init block of a command directly would instantiate
 * the WHOLE hierarchy instead of just what's needed.
 * TODO A much better solution is to just use maps and instantiate only the commands that are needed, without having
 *  a middleman like this
 */
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
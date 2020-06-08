package com.mineinabyss.idofront.commands

import com.mineinabyss.idofront.commands.arguments.ArgumentParser
import org.bukkit.command.CommandSender

/**
 * A template for creating a [Command] (through [newInstance]). We re-instantiate everything in the command hierarchy
 * to keep every instance separate from any other, but the issue was that we need to find a subcommand of a subcommand
 * by name, which is only accessible once instantiated, and running an init block of a command directly would instantiate
 * the WHOLE hierarchy instead of just what's needed.
 */
class CommandCreation(
        val names: List<String>,
        private val topPermission: String,
        private val sharedInit: List<Command.() -> Unit> = listOf(),
        val description: String,
        private val init: Command.() -> Unit,
        val argumentParser: ArgumentParser? = null
) {
    fun newInstance(sender: CommandSender, args: List<String>): Command =
            Command(names, sender, argumentParser ?: ArgumentParser(args), topPermission, sharedInit + init)
}
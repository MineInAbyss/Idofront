package com.mineinabyss.idofront.commands.execution

import com.mineinabyss.idofront.commands.entrypoint.CommandDSLEntrypoint
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

/**
 * Manages linking spigot's [CommandExecutor.onCommand] events to a [CommandDSLEntrypoint] inside
 */
abstract class IdofrontCommandExecutor : CommandExecutor {
    abstract val commands: CommandDSLEntrypoint

    /** Gets the command or send the player a message if it isn't found */
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        commands.execute(
            command.name,
            sender,
            args.toList()
        )
        return true
    }

    /** The starting block for the command DSL. */
    fun commands(plugin: JavaPlugin, init: CommandDSLEntrypoint.() -> Unit): CommandDSLEntrypoint {
        val commandEntrypoint = CommandDSLEntrypoint(plugin, this)
        commandEntrypoint.init()
        return commandEntrypoint
    }
}

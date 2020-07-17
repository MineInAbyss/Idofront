package com.mineinabyss.idofront.commands.execution

import com.mineinabyss.idofront.commands.BaseCommand
import com.mineinabyss.idofront.commands.Tag
import org.bukkit.entity.Player

//TODO better name than executions for the things that get executed

/**
 * An object that gets instantiated whenever a command gets run. We do this to have easy access to information like
 * the sender or arguments.
 */
open class Execution(val command: BaseCommand) : Tag {
    val sender = command.sender
    val arguments = command.strings
}

class PlayerExecution(command: BaseCommand) : Execution(command) {
    val player = sender as Player
}
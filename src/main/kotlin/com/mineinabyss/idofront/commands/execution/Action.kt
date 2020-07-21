package com.mineinabyss.idofront.commands.execution

import com.mineinabyss.idofront.commands.BaseCommand
import com.mineinabyss.idofront.commands.CommandDSLElement

/**
 * An action to be run when the a command is executed. Within its scope is extra information about the command that is
 * useful when the command is actually run.
 */
open class Action(val command: BaseCommand) : CommandDSLElement {
    val sender = command.sender
    val arguments = command.strings
}
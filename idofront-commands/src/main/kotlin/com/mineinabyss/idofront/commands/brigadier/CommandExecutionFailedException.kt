package com.mineinabyss.idofront.commands.brigadier

import net.kyori.adventure.text.Component

/**
 * Thrown when a command has failed to execute for any reason.
 *
 * It is used to stop the command from executing further in the DSL.
 */
data class CommandExecutionFailedException(
    val replyWith: Component? = null,
) : Exception()
package com.mineinabyss.idofront.commands

internal val commandRegisterer by lazy { CommandRegisterer() }

class CommandRegisterer {
    private val commandsList = mutableListOf<Command>()

    operator fun get(commandName: String): Command? =
            commandsList.firstOrNull { it.names.any { name -> name == commandName } }

    fun addCommands(commands: CommandHolder) = commandsList.addAll(commands.commands)
}
package com.mineinabyss.idofront.commands

internal val commandRegisterer by lazy { CommandRegisterer() }

class CommandRegisterer {
    private val commandsList = mutableListOf<Command>()
    operator fun get(commandName: String): Command? =
            commandsList.firstOrNull { it.name == commandName }

    fun addCommands(commands: Commands) = commandsList.addAll(commands.commands)
}
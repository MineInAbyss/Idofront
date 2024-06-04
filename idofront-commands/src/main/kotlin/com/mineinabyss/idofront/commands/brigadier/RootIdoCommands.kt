package com.mineinabyss.idofront.commands.brigadier

import io.papermc.paper.command.brigadier.Commands

@Suppress("UnstableApiUsage")
class RootIdoCommands(
    val commands: Commands,
) {
    private val rootCommands = mutableListOf<IdoRootCommand>()

    operator fun String.invoke(description: String? = null, init: IdoRootCommand.() -> Unit) {
        rootCommands += IdoRootCommand(
            Commands.literal(this),
            this,
            description,
        ).apply(init)
    }

    fun buildEach() {
        rootCommands.forEach { command ->
            command.applyToInitial()
            commands.register(
                command.initial.build(),
                command.description
            )
        }
    }
}

package com.mineinabyss.idofront.commands.brigadier

import io.papermc.paper.command.brigadier.Commands
import org.bukkit.plugin.Plugin

@Suppress("UnstableApiUsage")
class RootIdoCommands(
    val commands: Commands,
    val plugin: Plugin,
) {
    private val rootCommands = mutableListOf<IdoRootCommand>()

    operator fun String.invoke(aliases: List<String> = emptyList(), description: String? = null, init: IdoRootCommand.() -> Unit) {
        rootCommands += IdoRootCommand(
            Commands.literal(this),
            this,
            description,
            aliases,
            plugin,
        ).apply(init)
    }

    operator fun List<String>.invoke(description: String? = null, init: IdoRootCommand.() -> Unit) =
        firstOrNull()?.invoke(aliases = drop(1), description = description, init = init)

    operator fun String.div(other: String) = listOf(this, other)
    operator fun List<String>.div(other: String) = listOf(this) + other

    fun buildEach() {
        rootCommands.forEach { command ->
            commands.register(
                command.build(),
                command.description,
                command.aliases
            )
        }
    }
}

package com.mineinabyss.idofront.commands.brigadier

import io.papermc.paper.command.brigadier.Commands
import org.bukkit.plugin.Plugin

@Suppress("UnstableApiUsage")
class RootIdoCommands(
    val commands: Commands,
    val plugin: Plugin,
) {
    @PublishedApi
    internal val rootCommands = mutableListOf<IdoRootCommand>()

    /** Creates a new subcommand via a [Commands.literal] argument. */
    inline operator fun String.invoke(aliases: List<String> = emptyList(), description: String? = null, init: IdoRootCommand.() -> Unit) {
        rootCommands += IdoRootCommand(
            Commands.literal(this),
            this,
            description,
            aliases,
            plugin,
        ).apply(init)
    }

    /** Creates a new subcommand with aliases via a [Commands.literal] argument. */
    inline operator fun List<String>.invoke(description: String? = null, init: IdoRootCommand.() -> Unit) =
        firstOrNull()?.invoke(aliases = drop(1), description = description, init = init)

    /** Builder for commands with aliases. */
    operator fun String.div(other: String) = listOf(this, other)

    /** Builder for commands with aliases. */
    operator fun List<String>.div(other: String) = listOf(this) + other

    /** Builds and registers each root level command defined in the DSL. */
    @PublishedApi
    internal fun buildEach() {
        rootCommands.forEach { command ->
            commands.register(
                command.build(),
                command.description,
                command.aliases
            )
        }
    }
}

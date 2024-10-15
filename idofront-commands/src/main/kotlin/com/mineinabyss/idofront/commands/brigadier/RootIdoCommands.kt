package com.mineinabyss.idofront.commands.brigadier

import com.mojang.brigadier.tree.LiteralCommandNode
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
            val permission = command.permission ?: "${plugin.name.lowercase()}.${command.name}"
            commands.register(
                command.handlePermissions(permission).build(),
                command.description,
                command.aliases
            )
        }
    }

    private fun IdoCommand.handlePermissions(permission: String): IdoCommand {
        if (permission.isEmpty()) return this

        render().filterIsInstance<RenderedCommand.ThenFold>().forEach { render ->
            val command = render.initial.build().takeIf { it is LiteralCommandNode } ?: return@forEach
            render.initial.requires {
                it.sender.hasPermission("$permission.${command.name}")
            }
        }

        if (this.permission == null) requiresPermission(permission)

        return this
    }
}

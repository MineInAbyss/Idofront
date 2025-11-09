package com.mineinabyss.idofront.commands.brigadier

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.plugin.Plugin

@Annotations
class IdoRootCommand(
    initial: LiteralArgumentBuilder<CommandSourceStack>,
    name: String,
    val aliases: List<String>,
    plugin: Plugin,
) : IdoCommand(initial, name, plugin, parentPermission = null)

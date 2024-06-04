package com.mineinabyss.idofront.commands.brigadier

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack

@Annotations
@Suppress("UnstableApiUsage")
class IdoRootCommand(
    initial: LiteralArgumentBuilder<CommandSourceStack>,
    name: String,
    val description: String?,
) : IdoCommand(initial, name)

package com.mineinabyss.idofront.commands.brigadier

import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.entity.Player

@Annotations
@Suppress("UnstableApiUsage")
class IdoPlayerCommandContext(
    context: CommandContext<CommandSourceStack>,
): IdoCommandContext(context) {
    val player = executor as Player
}

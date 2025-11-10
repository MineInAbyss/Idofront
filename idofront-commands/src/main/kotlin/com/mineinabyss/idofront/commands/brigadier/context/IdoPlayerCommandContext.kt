package com.mineinabyss.idofront.commands.brigadier.context

import com.mineinabyss.idofront.commands.brigadier.Annotations
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.entity.Player

@Annotations
class IdoPlayerCommandContext(
    context: CommandContext<CommandSourceStack>,
    val player: Player = context.source.executor as Player,
) : IdoCommandContext(context)

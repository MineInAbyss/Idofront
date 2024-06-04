package com.mineinabyss.idofront.commands.brigadier

import com.mojang.brigadier.builder.ArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack

@Suppress("UnstableApiUsage")
internal typealias IdoArgBuilder = ArgumentBuilder<CommandSourceStack, *>

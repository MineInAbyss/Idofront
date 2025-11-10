package com.mineinabyss.idofront.commands.brigadier

import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.tree.CommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack

internal typealias IdoArgBuilder = ArgumentBuilder<CommandSourceStack, *>
internal typealias IdoCommandNode = CommandNode<CommandSourceStack>

fun IdoArgBuilder.thenCast(other: IdoArgBuilder): IdoArgBuilder = then(other) as IdoArgBuilder

fun IdoArgBuilder.thenCast(other: IdoCommandNode): IdoArgBuilder = then(other) as IdoArgBuilder

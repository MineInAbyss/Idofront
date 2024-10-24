package com.mineinabyss.idofront.commands.brigadier

import com.mojang.brigadier.LiteralMessage
import com.mojang.brigadier.Message
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import io.papermc.paper.command.brigadier.CommandSourceStack

interface IdoCommandParsingContext {
    val stack: CommandSourceStack

    fun fail(message: String): Nothing =
        throw SimpleCommandExceptionType(LiteralMessage(message)).create()

    fun fail(message: Message): Nothing =
        throw SimpleCommandExceptionType(message).create()
}

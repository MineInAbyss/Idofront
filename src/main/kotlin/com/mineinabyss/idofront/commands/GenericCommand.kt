package com.mineinabyss.idofront.commands

import com.mineinabyss.idofront.commands.arguments.ArgumentParser
import com.mineinabyss.idofront.commands.arguments.CommandArgument
import org.bukkit.command.CommandSender

abstract class GenericCommand : Containable() {
    abstract val sender: CommandSender
    abstract val argumentParser: ArgumentParser

    val sentArguments get() = argumentParser.size > depth

    fun addArgument(argument: CommandArgument<*>) = argumentParser.addArgument(argument)

    operator fun ArgumentParser.get(pos: Int) = get(depth, pos)
}
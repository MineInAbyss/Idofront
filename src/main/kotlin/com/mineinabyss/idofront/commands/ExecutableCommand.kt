package com.mineinabyss.idofront.commands

import com.mineinabyss.idofront.commands.arguments.ArgumentParser
import com.mineinabyss.idofront.commands.arguments.CommandArgument
import com.mineinabyss.idofront.commands.arguments.CommandArgumentBuilder
import org.bukkit.command.CommandSender
import kotlin.reflect.KProperty

abstract class ExecutableCommand : ChildContainingCommand() {
    abstract val sender: CommandSender
    abstract val argumentParser: ArgumentParser

    val sentArguments get() = argumentParser.size > 0

    fun addArgument(argument: CommandArgument<*>) = argumentParser.addArgument(argument)

    operator fun <T> CommandArgumentBuilder<T>.provideDelegate(thisRef: Any?, prop: KProperty<*>): CommandArgument<T> {
        val argument = CommandArgument<T>(this@ExecutableCommand, prop.name)
        init?.invoke(argument)
        this@ExecutableCommand.addArgument(argument)
        return argument
    }
}
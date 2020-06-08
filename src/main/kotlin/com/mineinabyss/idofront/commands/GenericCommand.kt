package com.mineinabyss.idofront.commands

import com.mineinabyss.idofront.commands.arguments.ArgumentParser
import com.mineinabyss.idofront.commands.arguments.CommandArgument
import com.mineinabyss.idofront.commands.arguments.CommandArgumentBuilder
import org.bukkit.command.CommandSender
import kotlin.reflect.KProperty

abstract class GenericCommand : Containable() {
    abstract val sender: CommandSender
    abstract val argumentParser: ArgumentParser
//    protected val _order = mutableListOf<Any>()
//    val order = _order.toList()
//    inline fun <reified T, R> List<T>.takeBefore(item: R) = order.takeWhile { it != item }.filterIsInstance<T>()

    val sentArguments get() = argumentParser.size > 0

    fun addArgument(argument: CommandArgument<*>) = argumentParser.addArgument(argument)

    operator fun <T> CommandArgumentBuilder<T>.provideDelegate(thisRef: Any?, prop: KProperty<*>): CommandArgument<T> {
        val argument = CommandArgument<T>(this@GenericCommand, prop.name)
        init?.invoke(argument)
        this@GenericCommand.addArgument(argument)
        return argument
    }
}
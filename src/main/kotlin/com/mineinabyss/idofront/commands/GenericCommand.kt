package com.mineinabyss.idofront.commands

import com.mineinabyss.idofront.commands.arguments.CommandArgument
import org.bukkit.command.CommandSender
import kotlin.reflect.KProperty

abstract class GenericCommand : Containable() {
    abstract val sender: CommandSender
    abstract val args: List<String>
    protected val arguments: MutableList<CommandArgument<*>> = mutableListOf()

    //ARGUMENT DELEGATION
    operator fun <T> CommandArgument<T>.getValue(thisRef: Any?, property: KProperty<*>) =
            getValue(this@GenericCommand)

    operator fun <T> CommandArgument<T>.setValue(thisRef: Any?, property: KProperty<*>, value: T) =
            setValue(this@GenericCommand, value)

    operator fun <R, T : CommandArgument<R>> T.unaryPlus(): T {
        arguments += this
        return this
    }

    fun addArgument(argument: CommandArgument<*>) = arguments.add(argument)
    fun addArguments(arguments: List<CommandArgument<*>>) = this.arguments.addAll(arguments)
}
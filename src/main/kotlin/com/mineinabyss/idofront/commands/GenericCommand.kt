package com.mineinabyss.idofront.commands

import com.mineinabyss.idofront.commands.arguments.ArgumentParser
import com.mineinabyss.idofront.commands.arguments.CommandArgument
import org.bukkit.command.CommandSender
import kotlin.reflect.KProperty

abstract class GenericCommand : Containable() {
    abstract val sender: CommandSender
    abstract val argumentParser: ArgumentParser

    val sentArguments get() = argumentParser.size > depth

    //ARGUMENT DELEGATION
    operator fun <T> CommandArgument<T>.getValue(thisRef: Any?, property: KProperty<*>) =
            getValue(this@GenericCommand)

    operator fun <T> CommandArgument<T>.setValue(thisRef: Any?, property: KProperty<*>, value: T) =
            setValue(this@GenericCommand, value)

    operator fun <R, T : CommandArgument<R>> T.unaryPlus(): T = addArgument(this).let { this }

    fun addArgument(argument: CommandArgument<*>) = argumentParser.addArgument(argument)

    operator fun ArgumentParser.get(pos: Int) = get(depth, pos)
}
package com.mineinabyss.idofront.commands.brigadier.context

import com.mineinabyss.idofront.commands.brigadier.Annotations
import com.mineinabyss.idofront.commands.brigadier.IdoArgument
import com.mineinabyss.idofront.commands.execution.CommandExecutionFailedException
import com.mineinabyss.idofront.textcomponents.miniMsg
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity

@Annotations
@Suppress("UnstableApiUsage")
open class IdoCommandContext(
    val context: CommandContext<CommandSourceStack>,
) {
    /** Stops the command, sending a [message] formatted with MiniMessage to its [sender]. */
    fun fail(message: String): Nothing = throw CommandExecutionFailedException("<red>$message".miniMsg())

    /** Stops the command, sending a [message] to its [sender]. */
    fun fail(message: Component): Nothing = throw CommandExecutionFailedException(message)

    /** The sender that ran this command. */
    val sender: CommandSender = context.source.sender

    val source get() = context.source

    /** An entity representing the [sender] on the server. */
    val executor: Entity? = source.executor

    val location: Location = source.location

    @JvmName("invoke2")
    inline operator fun <reified T> IdoArgument<T>.invoke(): T {
        return getArgumentOrNull<T>(this) ?: fail("<red>Argument $name not found".miniMsg())
    }

    @PublishedApi
    internal inline fun <reified T> getArgumentOrNull(argument: IdoArgument<T>): T? {
        val arg: Any = runCatching { context.getArgument(argument.name, Any::class.java) }.getOrNull()
            ?: return argument.default?.get?.invoke(this)
        return (argument.resolve?.invoke(this, arg) ?: arg) as T
    }

    @PublishedApi
    internal inline fun <reified T> arg(argument: IdoArgument<*>): T = (argument as IdoArgument<T>).invoke()
}

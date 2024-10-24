package com.mineinabyss.idofront.commands.brigadier

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
    fun fail(message: String): Nothing = throw CommandExecutionFailedException(message.miniMsg())

    /** Stops the command, sending a [message] to its [sender]. */
    fun fail(message: Component): Nothing = throw CommandExecutionFailedException(message)

    /** The sender that ran this command. */
    val sender: CommandSender = context.source.sender

    /** An entity representing the [sender] on the server. */
    val executor: Entity? = context.source.executor

    val location: Location = context.source.location

    @JvmName("invoke2")
    inline operator fun <reified T> IdoArgument<T>.invoke(): T {
        return getArgumentOrNull<T>(this)
            ?: fail("<red>Argument $name not found".miniMsg())
    }

    @PublishedApi
    internal inline fun <reified T> getArgumentOrNull(argument: IdoArgument<T>): T? =
        runCatching {
            val arg: Any = context.getArgument(argument.name, Any::class.java)
                ?: return@runCatching null
            (argument.resolve?.invoke(context.source, arg) ?: arg) as T
        }.getOrNull() ?: argument.default?.invoke(this)

    @PublishedApi
    internal inline fun <reified T> arg(argument: IdoArgument<*>): T = (argument as IdoArgument<T>).invoke()
}

package com.mineinabyss.idofront.commands.brigadier

import com.mineinabyss.idofront.commands.execution.CommandExecutionFailedException
import com.mineinabyss.idofront.textcomponents.miniMsg
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.argument.resolvers.ArgumentResolver
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
    fun commandException(message: String): Nothing = throw CommandExecutionFailedException(message.miniMsg())

    /** Stops the command, sending a [message] to its [sender]. */
    fun commandException(message: Component): Nothing = throw CommandExecutionFailedException(message)

    /** The sender that ran this command. */
    val sender: CommandSender = context.source.sender

    /** An entity representing the [sender] on the server. */
    val executor: Entity? = context.source.executor

    val location: Location = context.source.location

    @JvmName("invoke1")
    inline operator fun <reified T> IdoArgument<out ArgumentResolver<T>>.invoke(): T {
        @Suppress("UNCHECKED_CAST") // getArgument logic ensures this cast always succeeds if the argument was registered
        return ((this as IdoArgument<Any?>).invoke() as ArgumentResolver<T>)
            .resolve(context.source)
    }

    @JvmName("invoke2")
    inline operator fun <reified T> IdoArgument<T>.invoke(): T {
        return context.getArgumentOrNull<T>(name)
            ?: commandException("<red>Argument $name not found".miniMsg())
    }

    @PublishedApi
    internal inline fun <reified T> CommandContext<CommandSourceStack>.getArgumentOrNull(name: String): T? = runCatching {
        context.getArgument(name, T::class.java)
    }.getOrNull()
}

package com.mineinabyss.idofront.commands.brigadier.arguments

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.papermc.paper.command.brigadier.MessageComponentSerializer
import io.papermc.paper.command.brigadier.argument.CustomArgumentType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import java.util.concurrent.CompletableFuture
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class DurationTypeArgument(val minDuration: Duration = Duration.ZERO) : CustomArgumentType.Converted<Duration, String> {
    override fun convert(nativeType: String): Duration {
        return runCatching {
            fromString(nativeType)?.takeIf { it >= minDuration }!!
        }.getOrElse {
            val message = MessageComponentSerializer.message().serialize(Component.text("Invalid duration $nativeType", NamedTextColor.RED))
            throw CommandSyntaxException(SimpleCommandExceptionType(message), message)
        }
    }

    override fun getNativeType(): ArgumentType<String> {
        return StringArgumentType.word()
    }

    override fun <S : Any> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder,
    ): CompletableFuture<Suggestions> {
        val messageComponent = MessageComponentSerializer.message().serialize(Component.text("look at this cool green tooltip!", NamedTextColor.GREEN))
        builder.suggest("10d", messageComponent)
        builder.suggest("10h", messageComponent)
        builder.suggest("10m", messageComponent)
        builder.suggest("10s", messageComponent)

        return builder.buildFuture()
    }

    private fun fromString(string: String): Duration? {
        val splitAt = string.indexOfFirst { it.isLetter() }.takeIf { it > 0 } ?: string.length
        val value = string.take(splitAt).toDouble()
        return when (string.drop(splitAt)) {
            "ms" -> value.milliseconds
            "t" -> (value.toInt() * 50).milliseconds
            "s" -> value.seconds
            "m" -> value.minutes
            "h" -> value.hours
            "d" -> value.days
            "w" -> value.days * 7
            "mo" -> value.days * 31
            else -> null
        }
    }
}
package com.mineinabyss.idofront.commands.brigadier

import com.github.shynixn.mccoroutine.bukkit.asyncDispatcher
import com.github.shynixn.mccoroutine.bukkit.scope
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.argument.CustomArgumentType
import kotlinx.coroutines.future.future
import org.bukkit.Bukkit
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KClass

data class IdoArgumentParser<T : Any, R>(
    val parse: (StringReader) -> T,
    val resolve: (CommandSourceStack, T) -> R,
) {
    fun <New> map(map: (CommandSourceStack, R) -> New): IdoArgumentParser<T, New> =
        IdoArgumentParser(parse, resolve = { stack, value -> map(stack, resolve(stack, value)) })
}

data class IdoArgumentType<T>(
    val nativeType: ArgumentType<Any>,
//    val nativeKClass: KClass<*>,
    val parser: IdoArgumentParser<*, T>,
    val suggestions: (CommandContext<Any>, SuggestionsBuilder) -> CompletableFuture<Suggestions>,
    val commandExamples: MutableCollection<String>,
    val default: ((IdoCommandContext) -> T)? = null,
) : ArgumentType<T> {
    fun createType() = object : CustomArgumentType<Any, Any> {
        override fun parse(reader: StringReader): Any = parser.parse(reader)

        override fun <S : Any?> listSuggestions(
            context: CommandContext<S>,
            builder: SuggestionsBuilder,
        ) = suggestions(context as CommandContext<Any>, builder)

        override fun getExamples() = this@IdoArgumentType.commandExamples
        override fun getNativeType(): ArgumentType<Any> = this@IdoArgumentType.nativeType
    }

    override fun parse(reader: StringReader?) =
        error("IdoArgumentType should not be parsed directly, call createType() instead.")

    inline fun suggests(crossinline suggestions: suspend IdoSuggestionsContext.() -> Unit): IdoArgumentType<T> =
        copy(
            suggestions = { context, builder ->
                val plugin = Bukkit.getPluginManager().getPlugin("Idofront")!!
                plugin.scope.future(plugin.asyncDispatcher) {
                    suggestions(IdoSuggestionsContext(context as CommandContext<CommandSourceStack>, builder))
                    builder.build()
                }
            }
        )

    fun suggests(provider: SuggestionProvider<CommandSourceStack>): IdoArgumentType<T> = copy(
        suggestions = { context, suggestions ->
            provider.getSuggestions(
                context as CommandContext<CommandSourceStack>,
                suggestions
            )
        },
    )

    fun default(default: IdoCommandContext.() -> T): IdoArgumentType<T> =
        copy(default = default)

    inline fun <R> map(crossinline transform: IdoCommandParsingContext.(T) -> R): IdoArgumentType<R> =
        IdoArgumentType(
            nativeType = nativeType,
//            nativeKClass = nativeKClass,
            parser = parser.map { stack, value ->
                val context = object : IdoCommandParsingContext {
                    override val stack = stack
                }
                transform(context, value)
            },
            suggestions = suggestions,
            commandExamples = commandExamples
        )

}

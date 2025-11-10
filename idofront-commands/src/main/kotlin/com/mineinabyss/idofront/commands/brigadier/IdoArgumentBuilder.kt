package com.mineinabyss.idofront.commands.brigadier

import com.github.shynixn.mccoroutine.bukkit.asyncDispatcher
import com.github.shynixn.mccoroutine.bukkit.scope
import com.mineinabyss.idofront.commands.brigadier.context.IdoCommandContext
import com.mineinabyss.idofront.commands.brigadier.context.IdoSuggestionsContext
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import kotlinx.coroutines.future.future
import org.bukkit.Bukkit
import java.util.concurrent.CompletableFuture

data class Default<T>(
    val get: IdoCommandContext.() -> T,
    val permissionSuffix: String?,
)

data class IdoArgumentType<T>(
    val nativeType: ArgumentType<Any>,
    val resolve: ((IdoCommandContext, Any) -> T)? = null,
    val suggestions: ((CommandContext<Any>, SuggestionsBuilder) -> CompletableFuture<Suggestions>)? = null,
    val commandExamples: MutableCollection<String>,
    val default: Default<T>? = null,
) : ArgumentType<T> {
    fun createType() = nativeType

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

    fun default(permissionSuffix: String? = null, default: IdoCommandContext.() -> T): IdoArgumentType<T> =
        copy(default = Default(default, permissionSuffix))

    inline fun <R> map(crossinline transform: IdoCommandContext.(T) -> R): IdoArgumentType<R> =
        IdoArgumentType(
            nativeType = nativeType,
            resolve = { context, value ->
                resolve
                    ?.let { transform(context, it(context, value)) }
                    ?: transform(context, value as T)
            },
            suggestions = suggestions,
            commandExamples = commandExamples,
        )
}

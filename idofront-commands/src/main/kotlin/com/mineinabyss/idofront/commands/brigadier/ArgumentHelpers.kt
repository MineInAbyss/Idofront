package com.mineinabyss.idofront.commands.brigadier

import com.mineinabyss.idofront.commands.brigadier.context.IdoCommandContext
import com.mineinabyss.idofront.commands.brigadier.context.IdoSuggestionsContext
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.suggestion.SuggestionProvider
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.argument.CustomArgumentType
import io.papermc.paper.command.brigadier.argument.resolvers.ArgumentResolver

fun <T : Any> ArgumentType<T>.toIdo(): IdoArgumentType<T> {
    if (this is IdoArgumentType<T>) return this
    return IdoArgumentType(
        nativeType = (when (this) {
            is CustomArgumentType<*, *> -> nativeType
            is IdoArgumentType<T> -> nativeType
            else -> this
        }) as ArgumentType<Any>,
        suggestions = null,
        commandExamples = mutableListOf()
    )
}

fun <R : ArgumentResolver<T>, T> ArgumentType<R>.resolve(): IdoArgumentType<T> = toIdo().let {
    IdoArgumentType(
        nativeType = it.nativeType,
        resolve = { context, value -> (value as R).resolve(context.context.source) },
        suggestions = it.suggestions,
        commandExamples = it.commandExamples
    )
}

inline fun <T : Any> ArgumentType<T>.suggests(crossinline suggestions: suspend IdoSuggestionsContext.() -> Unit) =
    toIdo().suggests(suggestions)

fun <T : Any> ArgumentType<T>.default(permissionSuffix: String? = null, default: IdoCommandContext.() -> T): IdoArgumentType<T> =
    toIdo().copy(default = Default(default, permissionSuffix))

fun <T : Any> ArgumentType<T>.suggests(provider: SuggestionProvider<CommandSourceStack>) =
    toIdo().suggests(provider)

inline fun <T : Any, R> ArgumentType<T>.map(crossinline transform: IdoCommandContext.(T) -> R) =
    toIdo().map(transform)

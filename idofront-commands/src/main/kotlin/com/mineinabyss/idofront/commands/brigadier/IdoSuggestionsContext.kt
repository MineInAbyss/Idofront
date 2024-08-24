package com.mineinabyss.idofront.commands.brigadier

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack

@Suppress("UnstableApiUsage")
data class IdoSuggestionsContext(
    val context: CommandContext<CommandSourceStack>,
    val suggestions: SuggestionsBuilder,
) {
    /** The argument currently being typed*/
    val argument get() = suggestions.remaining

    /** The full input string */
    val input get() = suggestions.input

    /** Add a suggestion. */
    fun suggest(name: String) {
        suggestions.suggest(name)
    }

    /** Add a list of suggestions. */
    fun suggest(list: List<String>) {
        list.forEach { suggest(it) }
    }

    /** Add a suggestion, filtering it as the user types. */
    fun suggestFiltering(name: String) {
        if (name.startsWith(argument, ignoreCase = true))
            suggest(name)
    }

    /** Add a list of suggestions, filtering them as the user types. */
    fun suggestFiltering(list: List<String>) {
        list.forEach { suggestFiltering(it) }
    }
}

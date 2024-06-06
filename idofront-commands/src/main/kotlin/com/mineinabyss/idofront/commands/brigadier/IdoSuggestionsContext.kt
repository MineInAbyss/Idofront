package com.mineinabyss.idofront.commands.brigadier

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack

@Suppress("UnstableApiUsage")
data class IdoSuggestionsContext(
    val context: CommandContext<CommandSourceStack>,
    val suggestions: SuggestionsBuilder,
) {
    /** Add a suggestion, filtering it as the user types. */
    fun suggestFiltering(name: String) {
        if (name.startsWith(suggestions.remaining, ignoreCase = true))
            suggestions.suggest(name)
    }

    /** Add a list of suggestions, filtering them as the user types. */
    fun suggest(list: List<String>) {
        list.forEach { suggestFiltering(it) }
    }
}

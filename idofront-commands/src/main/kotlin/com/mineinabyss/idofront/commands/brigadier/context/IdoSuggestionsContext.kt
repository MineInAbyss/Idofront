package com.mineinabyss.idofront.commands.brigadier.context

import com.mojang.brigadier.Message
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.context.StringRange
import com.mojang.brigadier.suggestion.Suggestion
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack

class IdoSuggestionsContext(
    context: CommandContext<CommandSourceStack>,
    val suggestions: SuggestionsBuilder,
) : IdoCommandContext(context) {
    /** The argument currently being typed*/
    val argument get() = suggestions.remaining

    /** The full input string */
    val input get() = suggestions.input

    /** Add a suggestion. */
    fun suggest(name: String) {
        suggestions.suggest(name)
    }

    /** Add a suggestion with a tooltip */
    fun suggest(name: String, tooltip: () -> String) {
        suggestions.suggest(name, tooltip)
    }

    /** Add a suggestion with a tooltip */
    fun suggest(name: String, tooltip: Message) {
        suggestions.suggest(name, tooltip)
    }

    /** Add a list of suggestions. */
    fun suggest(list: List<String>) {
        list.forEach { suggest(it) }
    }

    /** Add a list of suggestions with a tooltip. */
    fun suggest(list: List<String>, tooltip: () -> String) {
        list.forEach { suggest(it, tooltip) }
    }

    /** Add a list of suggestions with a tooltip. */
    fun suggest(list: List<String>, tooltip: Message) {
        list.forEach { suggest(it, tooltip) }
    }

    /** Add a suggestion, filtering it as the user types. */
    fun suggestFiltering(name: String) {
        if (name.startsWith(argument, ignoreCase = true))
            suggest(name)
    }

    /** Add a suggestion with a tooltip, filtering it as the user types. */
    fun suggestFiltering(name: String, tooltip: () -> String) {
        if (name.startsWith(argument, ignoreCase = true))
            suggest(name, tooltip)
    }

    /** Add a suggestion with a tooltip, filtering it as the user types. */
    fun suggestFiltering(name: String, tooltip: Message) {
        if (name.startsWith(argument, ignoreCase = true))
            suggest(name, tooltip)
    }

    /** Add a list of suggestions, filtering them as the user types. */
    fun suggestFiltering(list: List<String>) {
        list.forEach { suggestFiltering(it) }
    }

    /** Add a list of suggestions with a tooltip, filtering them as the user types. */
    fun suggestFiltering(list: List<String>, tooltip: () -> String) {
        list.forEach { suggestFiltering(it, tooltip) }
    }

    /** Add a list of suggestions with a tooltip, filtering them as the user types. */
    fun suggestFiltering(list: List<String>, tooltip: Message) {
        list.forEach { suggestFiltering(it, tooltip) }
    }

    private val ranked = mutableListOf<String>()

    /**
     * Add every name containing what the user typed, best match first.
     * Names starting with it come first, then namespaced ids whose key does, then the rest by how early the match is, shorter first.
     * The client moves names starting with the input to the front itself, so ranking those first keeps both orders the same.
     *
     * Kept in that order on the client, where [suggest] arrives sorted alphabetically
     */
    fun suggestRanked(names: Collection<String>, limit: Int = Int.MAX_VALUE) {
        val typed = argument.lowercase()
        ranked += names.mapNotNull { name ->
            val full = name.lowercase()
            val key = full.substringAfter(':')
            val rank = when {
                full.startsWith(typed) -> 0
                key.startsWith(typed) -> 1
                typed in key -> 2
                typed in full -> 3
                else -> return@mapNotNull null
            }
            Triple(rank, key.indexOf(typed).takeIf { it >= 0 } ?: full.indexOf(typed), name)
        }.sortedWith(compareBy({ it.first }, { it.second }, { it.third.length }, { it.third })).take(limit).map { it.third }
    }

    // Suggestions.create sorts what SuggestionsBuilder.build hands it, so ranked ones are put together here instead
    @PublishedApi
    internal fun build(): Suggestions {
        val built = suggestions.build()
        if (ranked.isEmpty()) return built
        val range = StringRange.between(suggestions.start, suggestions.input.length)
        return Suggestions(range, ranked.map { Suggestion(range, it) } + built.list)
    }
}

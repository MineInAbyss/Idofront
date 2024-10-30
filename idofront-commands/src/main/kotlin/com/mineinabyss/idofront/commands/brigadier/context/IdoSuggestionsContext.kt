package com.mineinabyss.idofront.commands.brigadier.context

import com.mojang.brigadier.LiteralMessage
import com.mojang.brigadier.Message
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack

@Suppress("UnstableApiUsage")
class IdoSuggestionsContext(
    context: CommandContext<CommandSourceStack>,
    val suggestions: SuggestionsBuilder,
): IdoCommandContext(context) {
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
}

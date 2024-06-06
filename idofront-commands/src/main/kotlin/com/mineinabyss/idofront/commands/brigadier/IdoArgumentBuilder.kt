package com.mineinabyss.idofront.commands.brigadier

import com.mojang.brigadier.arguments.ArgumentType

data class IdoArgumentBuilder<T>(
    val type: ArgumentType<out T>,
    val suggestions: (suspend IdoSuggestionsContext.() -> Unit)? = null,
)

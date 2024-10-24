package com.mineinabyss.idofront.commands.brigadier

import io.papermc.paper.command.brigadier.CommandSourceStack
import kotlin.reflect.KProperty

class IdoArgument<T>(
    val name: String,
    val resolve: ((CommandSourceStack, Any) -> T)? = null,
    val default: ((IdoCommandContext) -> T)? = null,
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): IdoArgument<T> {
        return this
    }
}


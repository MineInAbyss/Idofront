package com.mineinabyss.idofront.commands.brigadier

import com.mineinabyss.idofront.commands.brigadier.context.IdoCommandContext
import kotlin.reflect.KProperty

class IdoArgument<T>(
    val name: String,
    val resolve: ((IdoCommandContext, Any) -> T)? = null,
    val default: ((IdoCommandContext) -> T)? = null,
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): IdoArgument<T> {
        return this
    }
}


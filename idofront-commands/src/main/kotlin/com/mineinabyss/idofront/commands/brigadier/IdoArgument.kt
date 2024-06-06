package com.mineinabyss.idofront.commands.brigadier

import kotlin.reflect.KProperty

class IdoArgument<T>(
    val name: String,
    val default: (IdoCommandContext.() -> T)? = null,
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): IdoArgument<T> {
        return this
    }
}


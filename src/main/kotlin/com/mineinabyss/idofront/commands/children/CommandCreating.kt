package com.mineinabyss.idofront.commands.children

import com.mineinabyss.idofront.commands.Command

interface CommandCreating {
    operator fun String.div(other: String) = mutableListOf(this, other)
    operator fun MutableCollection<String>.div(other: String) = add(other).let { this }

    operator fun Collection<String>.invoke(desc: String = "", init: Command.() -> Unit = {}) =
            command(names = this.toTypedArray(), desc = desc, init = init)

    operator fun String.invoke(desc: String = "", init: Command.() -> Unit = {}) =
            command(names = arrayOf(this), desc = desc, init = init)

    fun command(vararg names: String, desc: String = "", init: Command.() -> Unit = {}): Command?
}
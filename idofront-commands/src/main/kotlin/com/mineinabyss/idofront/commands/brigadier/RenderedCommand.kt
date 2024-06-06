package com.mineinabyss.idofront.commands.brigadier

/**
 * [RenderStep]s get reduced into a list of commands that more directly represent Brigadier's builder structure.
 *
 * This lets us write more complex nodes more easily.
 */
sealed interface RenderedCommand {
    fun foldLeft(acc: IdoArgBuilder): IdoArgBuilder

    data class Apply(val apply: IdoArgBuilder.() -> Unit) : RenderedCommand {
        override fun foldLeft(acc: IdoArgBuilder) = acc.apply(apply)
    }

    data class ThenFold(val initial: IdoArgBuilder, val list: List<RenderedCommand>) : RenderedCommand {
        override fun foldLeft(acc: IdoArgBuilder) = acc.apply {
            then(list.fold(initial) { acc, next -> next.foldLeft(acc) })
        }
    }
}

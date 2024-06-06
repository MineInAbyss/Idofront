package com.mineinabyss.idofront.commands.brigadier

/**
 * These are emitted line by line to reflect what a user specifies in the DSL.
 */
@Suppress("UnstableApiUsage")
sealed interface RenderStep {
    fun reduce(rightAcc: List<RenderedCommand>): List<RenderedCommand>

    class Builder(val builder: IdoArgBuilder) : RenderStep {
        override fun reduce(rightAcc: List<RenderedCommand>): List<RenderedCommand> {
            return listOf(RenderedCommand.ThenFold(builder, rightAcc))
        }
    }

    class Command(val command: IdoCommand) : RenderStep {
        override fun reduce(rightAcc: List<RenderedCommand>): List<RenderedCommand> {
            return listOf(RenderedCommand.ThenFold(command.initial, command.render())) + rightAcc
        }
    }

    class Apply(val apply: IdoArgBuilder.() -> Unit) : RenderStep {
        override fun reduce(rightAcc: List<RenderedCommand>): List<RenderedCommand> {
            return listOf(RenderedCommand.Apply(apply)) + rightAcc
        }
    }
}

package com.mineinabyss.idofront.commands.brigadier

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

//    class Executes(
//        val on: RenderStep,
//        var executes: IdoCommandContext.() -> Unit = { },
//    ) : RenderStep {
//        @Suppress("UNCHECKED_CAST") // Paper's api always uses CommandSourceStack
//        override fun builder(): IdoArgBuilder = on.builder().executes { context ->
//            executes(IdoCommandContext(context))
//            com.mojang.brigadier.Command.SINGLE_SUCCESS
//        } as IdoArgBuilder
//    }

//    class Nested(
//        val previous: RenderStep,
//        val inner: RenderStep,
//    ) : RenderStep {
//        override fun builder(): IdoArgBuilder {
//            return previous.builder().apply {
//                then(inner.builder())
//            }
//        }
//    }
}

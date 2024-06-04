package com.mineinabyss.idofront.commands.brigadier

import com.mojang.brigadier.arguments.ArgumentType
import io.papermc.paper.command.brigadier.Commands

@Suppress("UnstableApiUsage")
sealed interface BuildStep {
    fun builder(): IdoArgBuilder

    class Built(val builder: IdoArgBuilder) : BuildStep {
        override fun builder() = builder
    }

    class Argument<T>(val name: String, val type: ArgumentType<T>) : BuildStep {
        override fun builder() = Commands.argument(name, type)
    }

    class Command(val command: IdoCommand) : BuildStep {
        override fun builder() = command.applyToInitial()
    }

    class Executes(
        val on: BuildStep,
        var executes: IdoCommandContext.() -> Unit = { },
    ) : BuildStep {
        @Suppress("UNCHECKED_CAST") // Paper's api always uses CommandSourceStack
        override fun builder(): IdoArgBuilder = on.builder().executes { context ->
            executes(IdoCommandContext(context))
            com.mojang.brigadier.Command.SINGLE_SUCCESS
        } as IdoArgBuilder
    }

    class Nested(
        val previous: BuildStep,
        val inner: BuildStep,
    ) : BuildStep {
        override fun builder(): IdoArgBuilder {
            return previous.builder().apply {
                then(inner.builder())
            }
        }
    }
}

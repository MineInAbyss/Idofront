package com.mineinabyss.idofront.commands.brigadier

import com.mineinabyss.idofront.commands.execution.CommandExecutionFailedException
import com.mineinabyss.idofront.textcomponents.miniMsg
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.entity.Player
import kotlin.reflect.KProperty

@Suppress("UnstableApiUsage")
@Annotations
open class IdoCommand(
    internal val initial: LiteralArgumentBuilder<CommandSourceStack>,
    val name: String,
) {
    val buildSteps = mutableListOf<BuildStep>(BuildStep.Built(initial))

    operator fun <T> ArgumentType<T>.provideDelegate(t: T, property: KProperty<*>): IdoArgument<T> {
        val arg = IdoArgument<T>(property.name)
        buildSteps += BuildStep.Argument(property.name, this)
        return arg
    }

    operator fun String.invoke(init: IdoCommand.() -> Unit) {
        nested(BuildStep.Command(IdoCommand(Commands.literal(this), this).apply(init)))
    }

    private fun nested(inner: BuildStep) {
        val last = buildSteps.last()
        buildSteps.removeLast()
        buildSteps += BuildStep.Nested(last, inner)
    }

    fun requires(init: CommandSourceStack.() -> Boolean) {
        builder {
            requires { init(it) }
        }
    }

    fun builder(apply: IdoArgBuilder.() -> ArgumentBuilder<*, *>) {
        val last = buildSteps.last()
        buildSteps.removeLast()
        buildSteps += BuildStep.Built(last.builder().apply() as IdoArgBuilder)
    }

    fun playerExecutes(run: IdoPlayerCommandContext.() -> Unit) {
        executes {
            if (executor !is Player) commandException("<red>This command can only be run by a player.".miniMsg())
            run(IdoPlayerCommandContext(context))
        }
    }

    fun executes(run: IdoCommandContext.() -> Unit) {
        builder {
            executes { context ->
                try {
                    run(IdoCommandContext(context))
                } catch (e: CommandExecutionFailedException) {
                    e.replyWith?.let { context.source.sender.sendMessage(it) }
                }
                com.mojang.brigadier.Command.SINGLE_SUCCESS
            }
        }
    }

    fun applyToInitial(): IdoArgBuilder {
        val start = buildSteps.last().builder()
        return buildSteps.map { it.builder() }.reduceRight()/*.drop(1).foldRight(start)*/ { step, acc ->

            @Suppress("UNCHECKED_CAST") // Implicitly guaranteed by Paper's API
            step.then(acc) as IdoArgBuilder
        }
    }
}

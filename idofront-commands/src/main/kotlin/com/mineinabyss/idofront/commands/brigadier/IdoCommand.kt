package com.mineinabyss.idofront.commands.brigadier

import com.github.shynixn.mccoroutine.bukkit.asyncDispatcher
import com.mineinabyss.idofront.commands.execution.CommandExecutionFailedException
import com.mineinabyss.idofront.textcomponents.miniMsg
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import kotlin.reflect.KProperty

data class IdoArgumentBuilder<T>(
    val type: ArgumentType<out T>,
    val suggestions: (suspend IdoSuggestionsContext.() -> Unit)? = null,
//    val default: (IdoCommandContext.() -> T)?,
)

data class IdoSuggestionsContext(
    val context: CommandContext<CommandSourceStack>,
    val suggestions: SuggestionsBuilder,
) {
    fun suggestFiltering(name: String) {
        if (name.startsWith(suggestions.remaining, ignoreCase = true))
            suggestions.suggest(name)
    }

    fun suggest(list: List<String>) {
        list.forEach { suggestFiltering(it) }
    }
}

@Suppress("UnstableApiUsage")
@Annotations
open class IdoCommand(
    internal val initial: LiteralArgumentBuilder<CommandSourceStack>,
    val name: String,
    val plugin: Plugin,
) {
    private val renderSteps = mutableListOf<RenderStep>()

    private fun add(step: RenderStep) {
        renderSteps += step
    }

    fun <T> ArgumentType<T>.suggests(suggestions: suspend IdoSuggestionsContext.() -> Unit): IdoArgumentBuilder<T?> {
        return IdoArgumentBuilder(this, suggestions)
    }

    fun <T> ArgumentType<T>.suggests(provider: SuggestionProvider<CommandSourceStack>): IdoArgumentBuilder<T?> {
        return IdoArgumentBuilder(this) { provider.getSuggestions(context, suggestions) }
    }
//
//    fun <T> ArgumentType<T>.orElse(default: IdoCommandContext.() -> T): DefaultingArg<T> {
//        return DefaultingArg(this, default)
//    }
//
//    fun <T> ArgumentType<T>.orError(): DefaultingArg<T> {
//        return DefaultingArg(this, null)
//    }

    operator fun <T> ArgumentType<T>.provideDelegate(t: T, property: KProperty<*>): IdoArgument<T> {
        val arg = IdoArgument<T>(property.name)
        add(RenderStep.Builder(Commands.argument(property.name, this)))
        return arg
    }

    operator fun <T> IdoArgumentBuilder<T>.provideDelegate(thisRef: Any?, property: KProperty<*>): IdoArgument<T?> {
        add(RenderStep.Builder(Commands.argument(property.name, type).apply {
            if (this@provideDelegate.suggestions != null)
                suggests { context, builder ->
                    CoroutineScope(plugin.asyncDispatcher).async {
                        this@provideDelegate.suggestions.invoke(IdoSuggestionsContext(context, builder))
                        builder.build()
                    }.asCompletableFuture()
                }
        }))
        return IdoArgument(property.name)
    }

    operator fun String.invoke(init: IdoCommand.() -> Unit) {
        add(RenderStep.Command(IdoCommand(Commands.literal(this), this, plugin).apply(init)))
    }

    fun requires(init: CommandSourceStack.() -> Boolean) = edit {
        requires { init(it) }
    }

    fun executes(run: IdoCommandContext.() -> Unit) = edit {
        executes { context ->
            try {
                run(IdoCommandContext(context))
            } catch (e: CommandExecutionFailedException) {
                e.replyWith?.let { context.source.sender.sendMessage(it) }
            }
            com.mojang.brigadier.Command.SINGLE_SUCCESS
        }
    }

    fun playerExecutes(run: IdoPlayerCommandContext.() -> Unit) {
        executes {
            if (executor !is Player) commandException("<red>This command can only be run by a player.".miniMsg())
            run(IdoPlayerCommandContext(context))
        }
    }

    fun edit(apply: IdoArgBuilder.() -> ArgumentBuilder<*, *>) {
        add(RenderStep.Apply { apply() as IdoArgBuilder })
    }

    internal fun render(): List<RenderedCommand> {
        return renderSteps.foldRight(listOf()) { step, acc ->
            step.reduce(acc)
        }
    }

    internal fun build(): LiteralCommandNode<CommandSourceStack> {
        render().fold(initial as IdoArgBuilder) { acc, curr ->
            curr.foldLeft(acc)
        }
        return initial.build()
    }
}

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
// acc.then(optional.etc()).etc()

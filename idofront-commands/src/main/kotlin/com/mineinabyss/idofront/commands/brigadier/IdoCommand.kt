package com.mineinabyss.idofront.commands.brigadier

import com.github.shynixn.mccoroutine.bukkit.asyncDispatcher
import com.mineinabyss.idofront.commands.execution.CommandExecutionFailedException
import com.mineinabyss.idofront.textcomponents.miniMsg
import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import kotlin.reflect.KProperty

@Suppress("UnstableApiUsage")
@Annotations
open class IdoCommand(
    internal val initial: LiteralArgumentBuilder<CommandSourceStack>,
    val name: String,
    val plugin: Plugin,
) {
    private val renderSteps = mutableListOf<RenderStep>()

    fun <T> ArgumentType<T>.suggests(suggestions: suspend IdoSuggestionsContext.() -> Unit): IdoArgumentBuilder<T> {
        return IdoArgumentBuilder(this, suggestions)
    }

    fun <T> ArgumentType<T>.suggests(provider: SuggestionProvider<CommandSourceStack>): IdoArgumentBuilder<T> {
        return IdoArgumentBuilder(this) { provider.getSuggestions(context, suggestions) }
    }

    operator fun <T> ArgumentType<T>.provideDelegate(thisRef: Any?, property: KProperty<*>): IdoArgument<T> {
        // If no suggestions are provided, use the default listSuggestions method
        add(RenderStep.Builder(Commands.argument(property.name, this).apply {
            suggests { context, builder ->
                // Call the default listSuggestions method on the ArgumentType
                this@provideDelegate.listSuggestions(context, builder)
            }
        }))

        // Return an IdoArgument object with the argument's name
        return IdoArgument(property.name)
    }

    operator fun <T> IdoArgumentBuilder<T>.provideDelegate(thisRef: Any?, property: KProperty<*>): IdoArgument<T> {
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

    /** Creates a subcommand using [Commands.literal]. */
    inline operator fun String.invoke(init: IdoCommand.() -> Unit) {
        add(RenderStep.Command(IdoCommand(Commands.literal(this), this, plugin).apply(init)))
    }

    inline operator fun List<String>.invoke(init: IdoCommand.() -> Unit) {
        forEach { it.invoke { init() } }
    }

    operator fun String.div(other: String) = listOf(this, other)
    operator fun List<String>.div(other: String) = this + other

    /** Specifies a predicate for the command to execute further, may be calculated more than once. */
    inline fun requires(crossinline init: CommandSourceStack.() -> Boolean) = edit {
        requires { init(it) }
    }

    /** The permission to use for this command. If null, use default of plugin.commandname. If it is blank, require no permission */
    var permission: String? = null
    fun requiresPermission(permission: String) {
        this.permission = permission
    }

    /** Specifies an end node for the command that runs something, only one executes block can run per command execution. */
    inline fun executes(crossinline run: IdoCommandContext.() -> Unit) = edit {
        executes { context ->
            try {
                run(IdoCommandContext(context))
            } catch (e: CommandExecutionFailedException) {
                e.replyWith?.let { context.source.sender.sendMessage(it) }
            }
            com.mojang.brigadier.Command.SINGLE_SUCCESS
        }
    }

    /** [executes], ensuring the executor is a player. */
    inline fun playerExecutes(crossinline run: IdoPlayerCommandContext.() -> Unit) {
        executes {
            if (executor !is Player) commandException("<red>This command can only be run by a player.".miniMsg())
            run(IdoPlayerCommandContext(context))
        }
    }

    @PublishedApi
    internal fun add(step: RenderStep) {
        renderSteps += step
    }

    /** Directly edit the command in Brigadier. */
    inline fun edit(crossinline apply: IdoArgBuilder.() -> ArgumentBuilder<*, *>) {
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

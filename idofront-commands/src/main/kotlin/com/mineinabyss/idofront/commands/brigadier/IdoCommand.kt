package com.mineinabyss.idofront.commands.brigadier

import com.mineinabyss.idofront.commands.brigadier.context.IdoCommandContext
import com.mineinabyss.idofront.commands.brigadier.context.IdoPlayerCommandContext
import com.mineinabyss.idofront.commands.execution.CommandExecutionFailedException
import com.mineinabyss.idofront.textcomponents.miniMsg
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

/**
 * @property initial The initial literal argument builder for the command, the dsl adds render steps to it,
 * which will get applied when [build] gets called.
 * @property name The name of the command.
 * @property plugin The plugin associated with the command.
 * @property parentPermission The permission used by the parent of this command, if any.
 */
@Annotations
open class IdoCommand(
    internal val initial: LiteralArgumentBuilder<CommandSourceStack>,
    val name: String,
    val plugin: Plugin,
    val parentPermission: String?,
) {
    //    private val renderSteps = mutableListOf<RenderStep2>()
    @PublishedApi
    internal var render: RenderStep = { it }

    var permission: String? = defaultPermission()
    var description: String? = null

    fun <T : Any> createArgument(argument: ArgumentType<T>, name: String): RequiredArgumentBuilder<CommandSourceStack, out Any> {
        val type = if (argument is IdoArgumentType<T>) argument.createType() else argument
        return Commands.argument(name, type).apply {
            (argument as? IdoArgumentType)?.suggestions?.let {
                suggests { context, builder ->
                    it(context as CommandContext<Any>, builder)
                }
            }
        }
    }

    fun <T : Any> createArgumentRef(argument: ArgumentType<T>, name: String): IdoArgument<T> {
        val resolve = (argument as? IdoArgumentType<T>)?.resolve
        val default = (argument as? IdoArgumentType<T>)?.default
        return IdoArgument(name, resolve, default)
    }

    /** Creates a subcommand using [Commands.literal]. */
    inline operator fun String.invoke(crossinline init: IdoCommand.() -> Unit) {
        val string = this
        edit {
            val subcommand = IdoCommand(Commands.literal(string), string, plugin, permission).apply(init)

            it.then(subcommand.build())
        }
    }

    /** Creates a subcommand with aliases using [Commands.literal]. */
    inline operator fun List<String>.invoke(crossinline init: IdoCommand.() -> Unit) {
        forEach { it.invoke { init() } }
    }

    operator fun String.div(other: String) = listOf(this, other)
    operator fun List<String>.div(other: String) = this + other

    /** Specifies a predicate for the command to execute further, may be calculated more than once. */
    fun requires(predicate: CommandSourceStack.() -> Boolean) = edit {
        it.requires { stack -> predicate(stack) }
    }

    val executes: ExecutesBuilder<IdoCommandContext>
        get() = ExecutesBuilder(
            permission = permission ?: defaultPermission(),
            createContext = ::IdoCommandContext
        )

    /** Gets the assumed permission for this command based on its [plugin], [parentPermission], and [name] */
    fun defaultPermission(): String {
        val safeName = name.replace('.', '_')
        val pluginName = plugin.name.lowercase()
        return when {
            parentPermission != null -> "$parentPermission.$safeName"
            pluginName == safeName -> pluginName
            else -> "$pluginName.$safeName"
        }
    }

    /** Directly edit the command in Brigadier. */
    @PublishedApi
    internal inline fun edit(crossinline nextStep: RenderStep) {
        val previousStep = render
        render = {
            nextStep(previousStep(it))
        }
    }

    @PublishedApi
    internal fun build(): LiteralCommandNode<CommandSourceStack> {
        render.invoke(initial)
        return initial.build()
    }

    companion object {
        fun CommandSender.hasPermissionRecursive(permission: String): Boolean {
            val parts = permission.split(".")
            if (hasPermission(permission)) return true
            return (1..parts.size).any { hasPermission(parts.take(it).joinToString(".") + ".*") }
        }
    }

    inner class ExecutesBuilder<T : IdoCommandContext>(
        private val permission: String,
        private val createContext: (CommandContext<CommandSourceStack>) -> T,
    ) {
        fun withPermission(permission: String): ExecutesBuilder<T> = ExecutesBuilder(
            permission = permission,
            createContext = createContext
        )

        /** [executes], ensuring the executor is a player. */
        fun asPlayer() = ExecutesBuilder(
            permission = permission,
            createContext = {
                if (it.source.executor !is Player)
                    throw CommandExecutionFailedException("<red>This command can only be run by a player.".miniMsg())

                IdoPlayerCommandContext(it)
            },
        )

        fun asPlayer(block: IdoPlayerCommandContext.() -> Unit) {
            asPlayer().invoke(block)
        }

        /** Specifies an end node for the command that runs something, only one executes block can run per command execution. */
        operator fun invoke(
            block: T.() -> Unit,
        ) = edit {
            invokeOn(it, block)
            it
        }

        internal fun invokeOn(command: IdoArgBuilder, block: T.() -> Unit) {
            command.apply {
                // Apply command permission
                permission
                    ?.takeIf { it.isNotEmpty() }
                    ?.let { perm -> requires { it.sender.hasPermissionRecursive(perm) } }

                executes { context ->
                    if (!context.source.sender.hasPermissionRecursive(permission)) {
                        context.source.sender.sendMessage("<red>You do not have permission to run this command.".miniMsg())
                        return@executes Command.SINGLE_SUCCESS
                    }

                    try {
                        block(createContext(context))
                    } catch (e: CommandExecutionFailedException) {
                        e.replyWith?.let { context.source.sender.sendMessage(it) }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        context.source.sender.sendMessage("<red>An error occurred while executing this command.".miniMsg())
                    }
                    Command.SINGLE_SUCCESS
                }
            }
        }

        @PublishedApi
        internal fun executesDefaulting(
            namedArgs: List<Pair<String, ArgumentType<*>>>,
            run: T.(arguments: List<IdoArgument<*>>) -> Unit,
        ) {
            if (namedArgs.isEmpty()) invoke { run(emptyList()) }
            val argumentRefs = namedArgs.map { createArgumentRef(it.second, it.first) }
            val arguments = namedArgs.map { createArgument(it.second, it.first) }
            val numRequired = namedArgs.size - argumentRefs.takeLastWhile { it.default != null }.size

            // Apply execute blocks for optional arguments
            arguments.drop(numRequired).forEach {
                invokeOn(it) { run(argumentRefs) }
            }

            edit {
                // Apply execute block for last required argument
                if (numRequired - 1 >= 0) {
                    invokeOn(arguments[numRequired - 1]) { run(argumentRefs) }
                } else { // If no arguments were required, the base command should be executable
                    invokeOn(it) { run(argumentRefs) }
                }

                // todo case with 1 argument
                val folded = arguments/*.take(numLeadingRequiredArgs)*/.reduce { acc, arg -> acc.then(arg) }

                it.then(folded)
//                it.then(required.apply {
//                    invokeOn(this) { run(argumentRefs) }
//                    arguments.drop(numLeadingRequiredArgs).foldIndexed(this) { index, acc, curr ->
//                        acc.then(curr.apply {
//                            invokeOn(this) { run(argumentRefs) }
//                        })
//                    }
//                })
            }
        }

        inline fun <reified A : Any> args(
            a: Pair<String, ArgumentType<A>>,
            crossinline run: T.(A) -> Unit,
        ) = executesDefaulting(listOf(a)) { run(arg<A>(it[0])) }

        inline fun <reified A : Any, reified B : Any> args(
            a: Pair<String, ArgumentType<A>>,
            b: Pair<String, ArgumentType<B>>,
            crossinline run: T.(A, B) -> Unit,
        ) = executesDefaulting(listOf(a, b)) { run(arg<A>(it[0]), arg<B>(it[1])) }

        inline fun <reified A : Any, reified B : Any, reified C : Any> args(
            a: Pair<String, ArgumentType<A>>,
            b: Pair<String, ArgumentType<B>>,
            c: Pair<String, ArgumentType<C>>,
            crossinline run: T.(A, B, C) -> Unit,
        ) = executesDefaulting(listOf(a, b, c)) { run(arg<A>(it[0]), arg<B>(it[1]), arg<C>(it[2])) }

        inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any> args(
            a: Pair<String, ArgumentType<A>>,
            b: Pair<String, ArgumentType<B>>,
            c: Pair<String, ArgumentType<C>>,
            d: Pair<String, ArgumentType<D>>,
            crossinline run: T.(A, B, C, D) -> Unit,
        ) = executesDefaulting(listOf(a, b, c, d)) { run(arg<A>(it[0]), arg<B>(it[1]), arg<C>(it[2]), arg<D>(it[3])) }

        inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any> args(
            a: Pair<String, ArgumentType<A>>,
            b: Pair<String, ArgumentType<B>>,
            c: Pair<String, ArgumentType<C>>,
            d: Pair<String, ArgumentType<D>>,
            e: Pair<String, ArgumentType<E>>,
            crossinline run: T.(A, B, C, D, E) -> Unit,
        ) = executesDefaulting(listOf(a, b, c, d, e)) { run(arg<A>(it[0]), arg<B>(it[1]), arg<C>(it[2]), arg<D>(it[3]), arg<E>(it[4])) }

        inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any, reified F : Any> args(
            a: Pair<String, ArgumentType<A>>,
            b: Pair<String, ArgumentType<B>>,
            c: Pair<String, ArgumentType<C>>,
            d: Pair<String, ArgumentType<D>>,
            e: Pair<String, ArgumentType<E>>,
            f: Pair<String, ArgumentType<F>>,
            crossinline run: T.(A, B, C, D, E, F) -> Unit,
        ) = executesDefaulting(listOf(a, b, c, d, e, f)) { run(arg<A>(it[0]), arg<B>(it[1]), arg<C>(it[2]), arg<D>(it[3]), arg<E>(it[4]), arg<F>(it[5])) }
    }
}

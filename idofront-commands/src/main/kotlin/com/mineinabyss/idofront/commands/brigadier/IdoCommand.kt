package com.mineinabyss.idofront.commands.brigadier

import com.mineinabyss.idofront.commands.CommandMarker
import com.mineinabyss.idofront.commands.brigadier.context.IdoCommandContext
import com.mineinabyss.idofront.commands.brigadier.context.IdoPlayerCommandContext
import com.mineinabyss.idofront.commands.execution.CommandExecutionFailedException
import com.mineinabyss.idofront.textcomponents.miniMsg
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import kotlin.reflect.KProperty

/**
 * @property initial The initial literal argument builder for the command, the dsl adds render steps to it,
 * which will get applied when [build] gets called.
 * @property name The name of the command.
 * @property plugin The plugin associated with the command.
 * @property parentPermission The permission used by the parent of this command, if any.
 */
@Suppress("UnstableApiUsage")
@Annotations
open class IdoCommand(
    internal val initial: LiteralArgumentBuilder<CommandSourceStack>,
    val name: String,
    val plugin: Plugin,
    val parentPermission: String?,
) {
    private val renderSteps = mutableListOf<RenderStep>()
    var permission: String? = defaultPermission()
    var description: String? = null

    fun <T : Any> registerArgument(argument: ArgumentType<T>, defaultName: String): IdoArgument<T> {
        val type = if (argument is IdoArgumentType<T>) argument.createType() else argument
        val name = if (argument is IdoArgumentType) argument.name ?: defaultName else defaultName
        // If no suggestions are provided, use the default listSuggestions method
        add(RenderStep.Builder(Commands.argument(name, type).apply {
            (argument as? IdoArgumentType)?.suggestions?.let {
                suggests { context, builder ->
                    it(context as CommandContext<Any>, builder)
                }
            }
        }))

        // Return an IdoArgument object with the argument's name
        return createArgumentRef(argument, name)
    }

    fun <T : Any> createArgumentRef(argument: ArgumentType<T>, defaultName: String): IdoArgument<T> {
        val resolve = (argument as? IdoArgumentType<T>)?.resolve
        val default = (argument as? IdoArgumentType<T>)?.default
        val name = if (argument is IdoArgumentType<*>) argument.name ?: defaultName else defaultName
        return IdoArgument(name, resolve, default)
    }

    /** Creates a subcommand using [Commands.literal]. */
    inline operator fun String.invoke(init: IdoCommand.() -> Unit) {
        add(RenderStep.Command(IdoCommand(Commands.literal(this), this, plugin, permission).apply(init)))
    }

    /** Creates a subcommand with aliases using [Commands.literal]. */
    inline operator fun List<String>.invoke(init: IdoCommand.() -> Unit) {
        forEach { it.invoke { init() } }
    }

    operator fun String.div(other: String) = listOf(this, other)
    operator fun List<String>.div(other: String) = this + other

    /** Specifies a predicate for the command to execute further, may be calculated more than once. */
    inline fun requires(crossinline init: CommandSourceStack.() -> Boolean) = edit {
        requires { init(it) }
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
    inline fun edit(crossinline apply: IdoArgBuilder.() -> ArgumentBuilder<*, *>) {
        add(RenderStep.Apply { apply() as IdoArgBuilder })
    }

    @PublishedApi
    internal fun add(step: RenderStep) {
        renderSteps += step
    }

    internal fun render(): List<RenderedCommand> {
        return renderSteps.foldRight(listOf()) { step, acc ->
            step.reduce(acc)
        }
    }

    internal fun build(): LiteralCommandNode<CommandSourceStack> {
        // Apply render steps to command sequentially
        render().fold(initial as IdoArgBuilder) { acc, curr ->
            curr.foldLeft(acc)
        }

        // Get a final built command from Brigadier
        return initial.build()
    }

    operator fun <T : Any> ArgumentType<T>.provideDelegate(thisRef: Any?, property: KProperty<*>): IdoArgument<T> {
        return registerArgument(this, property.name)
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

        @PublishedApi
        internal fun executesDefaulting(
            arguments: List<ArgumentType<*>>,
            run: T.(arguments: List<IdoArgument<*>>) -> Unit,
        ) {
            val trailingDefaultIndex =
                arguments.lastIndex - arguments.takeLastWhile { (it as? IdoArgumentType<*>)?.default != null }.size
            val refs = arguments.mapIndexed { index, it -> createArgumentRef(it, index.toString()) }

            if (trailingDefaultIndex == -1) invoke { run(refs) }

            arguments.foldIndexed(listOf<IdoArgument<*>>()) { index, acc, arg ->
                val registered = acc + registerArgument(arg, index.toString())
                if (index >= trailingDefaultIndex) {
                    val default = (arg as? IdoArgumentType<*>)?.default
                    // TODO
//                    if (default != null && default.permissionSuffix != null) {
//                        requiresPermission("$permission.${default.permissionSuffix}")
//                    }
                    invoke { run(registered + refs.drop(registered.size)) }
                }
                registered
            }
        }

        inline fun <reified A : Any> args(
            a: ArgumentType<A>,
            crossinline run: T.(A) -> Unit,
        ) = executesDefaulting(listOf(a)) { run(arg<A>(it[0])) }

        inline fun <reified A : Any, reified B : Any> args(
            a: ArgumentType<A>,
            b: ArgumentType<B>,
            crossinline run: T.(A, B) -> Unit,
        ) = executesDefaulting(listOf(a, b)) { run(arg<A>(it[0]), arg<B>(it[1])) }

        inline fun <reified A : Any, reified B : Any, reified C : Any> args(
            a: ArgumentType<A>,
            b: ArgumentType<B>,
            c: ArgumentType<C>,
            crossinline run: T.(A, B, C) -> Unit,
        ) = executesDefaulting(listOf(a, b, c)) { run(arg<A>(it[0]), arg<B>(it[1]), arg<C>(it[2])) }

        inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any> args(
            a: ArgumentType<A>,
            b: ArgumentType<B>,
            c: ArgumentType<C>,
            d: ArgumentType<D>,
            crossinline run: T.(A, B, C, D) -> Unit,
        ) = executesDefaulting(listOf(a, b, c, d)) { run(arg<A>(it[0]), arg<B>(it[1]), arg<C>(it[2]), arg<D>(it[3])) }

        inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any> args(
            a: ArgumentType<A>,
            b: ArgumentType<B>,
            c: ArgumentType<C>,
            d: ArgumentType<D>,
            e: ArgumentType<E>,
            crossinline run: T.(A, B, C, D, E) -> Unit,
        ) = executesDefaulting(listOf(a, b, c, d, e)) { run(arg<A>(it[0]), arg<B>(it[1]), arg<C>(it[2]), arg<D>(it[3]), arg<E>(it[4])) }

        inline fun <reified A : Any, reified B : Any, reified C : Any, reified D : Any, reified E : Any, reified F : Any> args(
            a: ArgumentType<A>,
            b: ArgumentType<B>,
            c: ArgumentType<C>,
            d: ArgumentType<D>,
            e: ArgumentType<E>,
            f: ArgumentType<F>,
            crossinline run: T.(A, B, C, D, E, F) -> Unit,
        ) = executesDefaulting(listOf(a, b, c, d, e, f)) { run(arg<A>(it[0]), arg<B>(it[1]), arg<C>(it[2]), arg<D>(it[3]), arg<E>(it[4]), arg<F>(it[5])) }
    }
}

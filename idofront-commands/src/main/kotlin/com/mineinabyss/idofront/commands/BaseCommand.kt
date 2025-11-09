package com.mineinabyss.idofront.commands

import com.mineinabyss.idofront.commands.arguments.Argumentable
import com.mineinabyss.idofront.commands.arguments.CommandArgument
import com.mineinabyss.idofront.commands.children.ChildRunning
import com.mineinabyss.idofront.commands.children.ChildSharing
import com.mineinabyss.idofront.commands.children.CommandCreating
import com.mineinabyss.idofront.commands.children.runChildCommand
import com.mineinabyss.idofront.commands.execution.Executable
import com.mineinabyss.idofront.commands.naming.Nameable
import com.mineinabyss.idofront.commands.permissions.Permissionable
import com.mineinabyss.idofront.commands.sender.Sendable
import kotlin.reflect.KProperty

@Deprecated("Use new command api instead")
interface BaseCommand : CommandDSLElement,
    Argumentable,
    ChildRunning,
    ChildSharing,
    CommandCreating,
    Executable,
    Nameable,
    Permissionable,
    Sendable {
    operator fun <T> (CommandArgument<T>.() -> Unit).provideDelegate(
        thisRef: Nothing?,
        prop: KProperty<*>
    ): CommandArgument<T> {
        val argument = CommandArgument<T>(this@BaseCommand, prop.name)
        invoke(argument)
        addArgument(argument)
        return argument
    }

    /**
     * Creates a subcommand that will run if the next argument passed matches one of its [names]
     *
     * @param desc The description for the command. Displayed when asked to enter sub-commands.
     */
    override fun command(vararg names: String, desc: String, init: Command.() -> Unit): Command? {
        val thisPerm = this.names[0]
        val subcommand = Command(
            nameChain = nameChain + names.first(),
            names = names.toList(),
            sender = sender,
            argumentParser = childParser(),
            parentPermission = if (parentPermission == null) thisPerm else "$parentPermission.$thisPerm",
            description = desc
        )
        return runChildCommand(subcommand, init)
    }

    /** Group commands which share methods or variables together, so commands outside this scope can't see them */
    fun commandGroup(init: CommandGroup.() -> Unit) = CommandGroup(
        this
    ).init()
}

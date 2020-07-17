package com.mineinabyss.idofront.commands

import com.mineinabyss.idofront.commands.arguments.Argumentable
import com.mineinabyss.idofront.commands.arguments.argumentsMet
import com.mineinabyss.idofront.commands.children.ChildContaining
import com.mineinabyss.idofront.commands.children.ChildManager
import com.mineinabyss.idofront.commands.execution.Executable
import com.mineinabyss.idofront.commands.execution.Execution
import com.mineinabyss.idofront.commands.permissions.PermissionManager
import com.mineinabyss.idofront.commands.permissions.Permissionable
import com.mineinabyss.idofront.commands.sender.Sendable
import com.mineinabyss.idofront.messaging.color
import com.mineinabyss.idofront.messaging.info
import org.bukkit.command.CommandSender

/**
 * A class for a command which will be instantiated by
 *
 * @param names A list of names for this command, when either is matched, the command will run.
 * @param sender The sender that ran this command (ex. console or player)
 * @param argumentParser A class that aids with parsing arguments passed to this command
 * @param parentPermission The parent's permissions
 *
 * @property executedCommand Whether any command was executed successfully up to the moment this is accessed.
 */
class Command(
        val names: List<String>,
        override val sender: CommandSender,
        argumentParser: Argumentable,
        parentPermission: String,
        init: List<Command.() -> Unit> //TODO find a way to move this outta here
) : BaseCommand,
        Argumentable by argumentParser,
        ChildContaining by ChildManager(),
        Executable,
        Permissionable by PermissionManager(parentPermission),
        Sendable {
    override var executedCommand = false

    init {
        init.forEach { it.invoke(this) }
        if (!executedCommand) {
            sendCommandDescription()
        }
    }

    override fun canExecute(): Boolean =
            permissionsMetFor(this)
                    && !firstArgumentIsFor(subcommands)
                    && argumentsMet()

    /** Called when the command should be executed */
    override fun <E : Execution> execute(run: E.() -> Unit, execution: E) {
        if (canExecute()) {
            execution.run()
            executedCommand = true
        }
    }

    override fun onExecute(run: Execution.() -> Unit) = this@Command.execute(run, Execution(this))

    override fun sendCommandDescription() {
        sender.info(("&6/${names.first()}&7" +
                if (names.size > 1) " (other aliases ${names.drop(1)})" else "" +
                        if (arguments.isNotEmpty()) " $argumentNames" else "").color())

        if (subcommands.isNotEmpty()) {
            sender.info(
                    (subcommands.mapIndexed { i, it ->
                        "&f   &6 ${if (i == subcommands.size - 1) "┗" else "┣"} &o${it.names.first()}&7 " +
                                if (it.description.isNotEmpty()) "- ${it.description}" else ""
                    }.joinToString(separator = "\n")).color()
            )
        }
    }
}
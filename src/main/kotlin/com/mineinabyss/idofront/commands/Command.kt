package com.mineinabyss.idofront.commands

import com.mineinabyss.idofront.commands.arguments.Argumentable
import com.mineinabyss.idofront.commands.arguments.argumentsMet
import com.mineinabyss.idofront.commands.children.ChildManager
import com.mineinabyss.idofront.commands.children.ChildRunning
import com.mineinabyss.idofront.commands.children.ChildSharing
import com.mineinabyss.idofront.commands.children.ChildSharingManager
import com.mineinabyss.idofront.commands.execution.Action
import com.mineinabyss.idofront.commands.execution.Executable
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
        override val nameChain: List<String>,
        override val names: List<String>,
        override val sender: CommandSender,
        argumentParser: Argumentable,
        parentPermission: String,
        override val description: String = ""
) : BaseCommand,
        Argumentable by argumentParser,
        ChildRunning by ChildManager(),
        ChildSharing by ChildSharingManager(),
        Executable,
        Permissionable by PermissionManager(parentPermission),
        Sendable {
    override var executedCommand = false

    fun runWith(init: Command.() -> Unit): Command {
        init()
        if (subcommands.isNotEmpty() && !executedCommand) sendCommandDescription()
        return this
    }

    override fun canExecute(): Boolean =
            permissionsMetFor(this)
                    && !firstArgumentIsFor(subcommands)
                    && argumentsMet()

    override fun <A : Action> A.execute(run: A.() -> Unit) {
        if (this@Command.canExecute()) {
            run()
            this@Command.executedCommand = true
        }
    }

    override fun action(run: Action.() -> Unit) {
        /*if(!argumentsWereSent) */Action(this).execute(run)
    }

    override fun sendCommandDescription() {
        sender.info(("&6/${nameChain.joinToString(separator = " ")}&7" +
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
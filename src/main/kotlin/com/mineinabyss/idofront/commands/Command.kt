package com.mineinabyss.idofront.commands

import com.mineinabyss.idofront.commands.arguments.ArgumentParser
import com.mineinabyss.idofront.commands.conditions.Condition
import com.mineinabyss.idofront.messaging.color
import com.mineinabyss.idofront.messaging.error
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

typealias ConditionLambda = Command.() -> Boolean
typealias CommandExtension = GenericCommand.() -> Unit

/**
 * A class for a command which will be instantiated by
 *
 * @param names A list of names for this command, when either is matched, the command will run.
 * @param sender The sender that ran this command (ex. console or player)
 * @param argumentParser A class that aids with parsing arguments passed to this command
 * @param parentPermission The parent's permissions
 * @param depth The cure
 *
 * @property executedCommand Whether any command was executed successfully up to the moment this is accessed.
 */
class Command(
        val names: List<String>,
        override val sender: CommandSender,
        override val argumentParser: ArgumentParser,
        override val parentPermission: String,
        init: List<Command.() -> Unit>
) : GenericCommand(), Permissionable {
    private val conditions = mutableListOf<Condition>()
    private val subcommands = mutableListOf<CommandCreation>() //TODO might be better to just have a list of command names
    override var permissions = mutableListOf(parentPermission)
    private var executedCommand = false
    private val firstArgument get() = argumentParser.strings[0]

    /** Called when the command should be executed */
    fun execute(executionInfo: ExecutionInfo) {
        //check whether sender has permission to run this command
        if (permissions.none { sender.hasPermission(it) || sender.hasPermission("$it.*") }) {
            sender.error(noPermissionMessage)
            //TODO probably need to throw an error to stop the command from running after this
            return
        }

        //don't run if the first argument is of a subcommand
        if (sentArguments && subcommands.any { it.names.contains(firstArgument) }) return

        //run the specified execution if conditions and arguments have been met
        if (conditionsMet() && argumentsMet()) with(executionInfo) {
            execute(create(sender, argumentParser))
            executedCommand = true
        } else {//TODO stop right here as well
        }
    }

    class ExecutionInfo(val execute: Execution.() -> Unit, val create: (CommandSender, ArgumentParser) -> Execution)

    //MUTABLE STUFF FOR DSL
    var permission
        get() = permissions[0]
        set(perm) = permissions.run { clear(); add(perm) }
    var noPermissionMessage: String = "You do not have the permission to run this command!"

    private fun addConditions(conditions: List<Condition>) = conditions.forEach { addCondition(it) }

    private fun addCondition(condition: Condition) {
        conditions += condition
    }

    /** Adds a [Condition] and passes the [Condition.check]. The command will run only if the check returns true. */
    fun onlyIf(condition: ConditionLambda): Condition = Condition(condition).also { addCondition(it) }

    //DSL FUNCTIONS
    //TODO does it make sense to say onExecute if this runs right away now. Might be better to just say execute, action, or run
    fun onExecute(run: Execution.() -> Unit) {
        execute(ExecutionInfo(run, { _, _ -> Execution() }))
    }

    /**
     * Creates a subcommand that will run if the next argument passed matches one of its [names]
     *
     * @param desc The description for the command. Displayed when asked to enter sub-commands.
     */
    fun command(vararg names: String, desc: String = "", init: Command.() -> Unit) {
        val subcommand = CommandCreation(names.toList(), "$parentPermission.${names[0]}", sharedInit, desc, init, argumentParser.childParser())
        runCommand(subcommand)
    }

    /** Group commands which share methods or variables together, so commands outside this scope can't see them */
    fun commandGroup(init: CommandGroup<Command>.() -> Unit) =
            CommandGroup(this, sender, argumentParser).init()

    override fun runCommand(subcommand: CommandCreation): CommandCreation {
        subcommands += subcommand

        //if there are extra arguments and sub-commands exist, we first try to match them to any sub-commands
        if (sentArguments && subcommand.names.contains(firstArgument)) {
            subcommand.newInstance(sender, argumentParser.strings.drop(1))
            executedCommand = true
        }
        return subcommand
    }

    /**
     * Verifies that all conditions defined in this command have been met and runs their success/failure actions.
     *
     * @return Whether all the conditions were met.
     */
    private fun conditionsMet() =
            conditions.filter { condition ->
                //if the check fails, run things that should run on failure
                //this must be done with filter and isEmpty since we want *all* failed actions to run
                //TODO decide whether it'd be better to only call fail on the first encountered failed condition

                val checkFailed = !condition.check(this)
                if (checkFailed) condition.fail(this)
                //filters so only failing conditions remain
                checkFailed
            }.isEmpty()

    /** Verifies that all arguments defined in this command have been correctly passed. */
    private fun argumentsMet() = argumentParser.verifyArgumentsFor(this)

    /**
     * An object that gets instantiated whenever a command gets run. We do this to have easy access to information like
     * the sender or arguments.
     */
    open inner class Execution : Tag() {
        val sender: CommandSender = this@Command.sender
        val arguments = this@Command.argumentParser.strings
    }

    inner class PlayerExecution : Execution() {
        val player = sender as Player
    }

    fun commandInfoText() {

    }

    init {
        init.forEach { it.invoke(this) }
        if (!executedCommand) {
            sender.error(("&6/${names.first()}&7" +
                    if (names.size > 1) " (other aliases ${names.drop(1)})" else "" +
                            if (argumentParser.argumentsSize > 0) " ${argumentParser.argumentNames}" else "").color())

            if (subcommands.isNotEmpty()) {
                sender.error(
                        (subcommands.mapIndexed { i, it ->
                            "&f   &6 ${if (i == subcommands.size - 1) "┗" else "┣"} &o${it.names.first()}&7 " +
                                    it.argumentParser?.argumentNames +
                                    if (it.description != "") " - ${it.description}" else ""
                        }.joinToString(separator = "\n")).color()
                )
            }
        }
    }
}
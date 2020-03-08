package com.mineinabyss.idofront.commands

import com.mineinabyss.idofront.commands.arguments.ArgumentParser
import com.mineinabyss.idofront.messaging.color
import com.mineinabyss.idofront.messaging.error
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

typealias ConditionLambda = Command.() -> Boolean
typealias CommandExtension = GenericCommand.() -> Unit

class Command(
        val names: List<String>,
        override val sender: CommandSender,
        override val argumentParser: ArgumentParser,
        override val permissionChain: String,
        override val depth: Int
) : GenericCommand(), Permissionable {
    private val conditions: MutableList<Condition> = mutableListOf()
    private val executions = mutableListOf<ExecutionInfo>()
    private val subcommands = mutableListOf<CommandCreation>()
    private val _permissions: MutableList<String> = mutableListOf()

    /**
     * Called when the command should be executed
     */
    fun execute() {
        if (this@Command._permissions.none { sender.hasPermission(it) }) {
            sender.error(noPermissionMessage)
            return
        }

        if (sentArguments && subcommands.isNotEmpty())
            subcommands.firstOrNull { it.names.contains(argumentParser[0]) } //look for a sub-command matching the first argument
                    //first argument is the second item in the list since the first is the current command's name
                    ?.let {
                        val command = it.newInstance(sender, argumentParser.args, depth + 1)//execute it if found, removing this argument from the list
                        command.execute()
                        return //stop here if found
                    }

        if (subcommands.isNotEmpty() && executions.isEmpty()) {
            val subCommandList = StringBuilder("&7━━━━━┫&6&l Pick a subcommand  &7┣━━━━━━\n&r".color())
            subcommands.map { it.names[0] }.forEachIndexed { i, it ->
                with(subCommandList) {
                    append("&6$it&7".color())
                    if (subcommands[i].description != "") append(":  ${subcommands[i].description}")
                    if (i < subcommands.size) append("\n")
                }
            }
            sender.error("$subCommandList")
            return
        }
        //run this command's executions and verify that all conditions have been met and arguments parsed correctly
        //we must verify arguments before checking conditions since some conditions may depend on arguments being parsed properly
        if (argumentParser.verifyArgumentsFor(this) && conditionsMet())
            executions.forEach { info ->
                val execution = info.create(sender, argumentParser)
                info.execute(execution)
            }
    }

    class ExecutionInfo(val execute: Execution.() -> Unit, val create: (CommandSender, ArgumentParser) -> Execution)

    fun addPermissions(vararg permissions: String) = _permissions.addAll(permissions)

    //MUTABLE STUFF FOR DSL
    override var permissions
        get() = _permissions.toList()
        set(perms) {
            _permissions.clear()
            _permissions.addAll(perms)
        }
    var permission
        get() = _permissions[0]
        set(perm) {
            _permissions.clear()
            _permissions.add(perm)
        }
    var noPermissionMessage: String = "You do not have the permission to run this command!"

    fun addExecution(execution: ExecutionInfo) = executions.add(execution)
    fun addConditions(conditions: List<Condition>) = this.conditions.addAll(conditions)

    //CONDITIONS
    /**
     * @property check The check to run on execution. If returns true, the command can proceed, if false, [fail] will
     * be called.
     */
    inner class Condition(val check: ConditionLambda) {
        private val runOnFail = mutableListOf<CommandExtension>()

        fun orElse(run: CommandExtension) = runOnFail.add(run).let { this }

        fun orElseError(error: String) = runOnFail.add { sender.error(error) }.let { this }

        internal fun fail(execution: GenericCommand) = runOnFail.forEach { it.invoke(execution) }
    }

    /**
     * Adds a [Condition] and passes the [Condition.check]. The command will run only if the check returns true.
     */
    fun onlyIf(condition: ConditionLambda): Condition = Condition(condition).also { conditions.add(it) }

    //DSL FUNCTIONS
    fun onExecute(run: Execution.() -> Unit) =
            addExecution(ExecutionInfo(run, { _, _ -> Execution() }))

    /**
     * @param desc The description for the command. Displayed when asked to ender subcommands.
     */
    fun command(vararg names: String, desc: String = "", init: Command.() -> Unit) {
        //TODO might need to do argumentParser.childParser()
        addChild(CommandCreation(names.toList(), "$permissionChain.${names[0]}", sharedInit, desc, init, argumentParser))
    }

    /**
     * Group commands which share methods or variables together, so commands outside this scope can't see them
     */
    fun commandGroup(init: CommandGroup<Command>.() -> Unit) {
        CommandGroup(this, sender, argumentParser).init()
    }

    override fun addChild(creation: CommandCreation): CommandCreation {
        subcommands += creation
        return creation
    }

    /**
     * Runs through all the conditions registered in this [Command] and runs their specified lambdas if
     * they failed.
     *
     * @return whether all the conditions were met
     */
    internal fun conditionsMet() =
            this@Command.conditions.filter { condition ->
                //if the check fails, run things that should run on failure
                //this must be done with filter and isEmpty since we want *all* failed actions to run
                //TODO decide whether it'd be better to only call fail on the first encountered failed condition

                val checkFailed = !condition.check(this)
                if (checkFailed) condition.fail(this)
                //filters so only failing conditions remain
                checkFailed
            }.isEmpty()

    /**
     * An object that gets instantiated whenever a command gets run. We do this to have easy access to information like
     * the sender or arguments.
     */
    open inner class Execution : Tag() {
        val sender: CommandSender = this@Command.sender
        val arguments = this@Command.argumentParser.args
    }

    inner class PlayerExecution : Execution() {
        val player = sender as Player
    }

    init {
        permissions = listOf(permissionChain)
    }
}
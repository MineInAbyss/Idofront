package com.mineinabyss.idofront.commands

import com.mineinabyss.idofront.commands.Command.Execution
import com.mineinabyss.idofront.messaging.error
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

typealias ConditionLambda = Execution.() -> Boolean
typealias ExecutionExtension = Execution.() -> Unit

open class Command(
        val names: List<String>,
        override val sender: CommandSender,
        override val args: List<String>,
        override val permissionChain: String
) : GenericCommand(), Permissionable {
    private val conditions: MutableList<Condition> = mutableListOf()
    private val executions = mutableListOf<ExecutionInfo<Execution, Execution>>()
    private val subcommands = mutableListOf<CommandCreation>()
    private val _permissions: MutableList<String> = mutableListOf()

    /**
     * Called when the command should be executed
     */
    fun execute() {
        if (args.isNotEmpty() && subcommands.isNotEmpty())
            subcommands.firstOrNull { it.names.contains(args[0]) } //look for a sub-command matching the first argument
                    //first argument is the second item in the list since the first is the current command's name
                    ?.let {
                        val command = it.newInstance(sender, args.drop(1))//execute it if found, removing this argument from the list
                        command.execute()
                        return //stop here if found
                    }

        if (subcommands.isNotEmpty() && executions.isEmpty()) {
            sender.error("Missing arguments, choose one of: ${subcommands.map { it.names[0] }}")
            return
        }
        //run this command's executions
        executions.forEach { info ->
            val execution = info.creation(sender, args)

            conditions.forEach { it.check(execution) } //"register" all the conditions on the cueerrent Execution object

            //verify all the conditions have been met and arguments parsed correctly
            if (execution.conditionsMet() && arguments.all { it.verifyAndCheckMissing(execution) }) {
                info.run(execution)
            }
        }
    }

    class ExecutionInfo<in T : Execution, out R : Execution>(val run: T.() -> Unit, val creation: (CommandSender, List<String>) -> R)

    fun addPermissions(vararg permissions: String) = _permissions.addAll(permissions)

    inner class PlayerExecution(sender: CommandSender, args: List<String>) : Execution(sender, args) {
        val player = sender as Player
    }

    //MUTABLE STUFF FOR DSL
    var permissions
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
            val test = mutableListOf<Number>()
            test.add(1.0)
        }
    var noPermissionMessage: String = "You do not have the permission to run this command!"

    fun addExecution(execution: ExecutionInfo<Execution, Execution>) = executions.add(execution)
    fun addConditions(conditions: List<Condition>) = this.conditions.addAll(conditions)

    //CONDITIONS
    /**
     * @property check The check to run on execution. If returns true, the command can proceed, if false, [fail] will
     * be called.
     */
    inner class Condition(val check: ConditionLambda) {
        private val runOnFail = mutableListOf<ExecutionExtension>()

        fun orElse(run: ExecutionExtension) = runOnFail.add(run).let { this }

        fun orElseError(error: String) = runOnFail.add { sender.error(error) }.let { this }

        internal fun fail(execution: Execution) = runOnFail.forEach { it.invoke(execution) }
    }

    /**
     * Adds a [Condition] and passes the [Condition.check]. The command will run only if the check returns true.
     */
    fun onlyIf(condition: ConditionLambda): Condition = Condition(condition).also { conditions.add(it) }

    //DSL FUNCTIONS
    fun onExecute(run: ExecutionExtension) =
            addExecution(ExecutionInfo(run, { sender, args -> Execution(sender, args) }))

    fun command(vararg names: String, init: Command.() -> Unit) {
        addChild(CommandCreation(names.toList(), "$permissionChain.${names[0]}", arguments, sharedInit, init))
    }

    /**
     * Group commands which share methods or variables together, so commands outside this scope can't see them
     */
    fun commandGroup(init: CommandGroup<Command>.() -> Unit) {
        CommandGroup(this, sender, args).init()
    }

    override fun addChild(creation: CommandCreation): CommandCreation {
        subcommands += creation
        return creation
    }

    fun applyShared() {
        sharedInit.forEach { this.it() }
    }

    /**
     * An object that gets instantiated whenever a command gets run. We do this to have easy access to information like
     * the sender or arguments.
     */
    open inner class Execution(val sender: CommandSender, val args: List<String>) : Tag() {
        /**
         * Runs through all the conditions registered in this [Command] and runs their specified lambdas if
         * they failed.
         *
         * @return whether all the conditions were met
         */
        fun conditionsMet() =
                this@Command.conditions.filter { condition ->
                    //if the check fails, run things that should run on failure
                    //this must be done with filter and isEmpty since we want *all* failed actions to run
                    //TODO decide whether it'd be better to only call fail on the first encountered failed condition

                    val checkFailed = !condition.check(this)
                    if (checkFailed) condition.fail(this)
                    //filters so only failing conditions remain
                    checkFailed
                }.isEmpty()
    }

    init {
        permissions = listOf(permissionChain)
        onlyIf { this@Command._permissions.any { sender.hasPermission(it) } }.orElseError(noPermissionMessage)
    }
}
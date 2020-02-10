package com.mineinabyss.idofront.commands

import com.mineinabyss.idofront.commands.Command.Execution
import com.mineinabyss.idofront.messaging.error
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.reflect.KProperty

typealias ConditionLambda = Execution.() -> Boolean
typealias ExecutionExtension = Execution.() -> Unit

open class GenericCommand(arguments: List<CommandArgument<*>>) : Tag() {
    protected val arguments: MutableList<CommandArgument<*>> = arguments.toMutableList()

    fun addArgument(argument: CommandArgument<*>) = arguments.add(argument)
    fun addArguments(arguments: List<CommandArgument<*>>) = this.arguments.addAll(arguments)

    //extensions for our supported argument types so you don't have to pass `this` each time
    fun StringArgument(order: Int, name: String, default: String? = null) = StringArgument(this, order, name, default).also { addArgument(it) }

    fun IntArgument(order: Int, name: String, default: Int? = null) = IntArgument(this, order, name, default).also { addArgument(it) }

    fun OptionArgument(order: Int, name: String, options: List<String>, default: String? = null) = OptionArgument(this, order, name, options, default).also { addArgument(it) }

    fun BooleanArgument(order: Int, name: String, default: Boolean? = null) = BooleanArgument(this, order, name, default).also { addArgument(it) }
}

open class Command(
        val names: List<String>,
        private val permissionChain: String,
        permission: String = permissionChain,
        private val conditions: MutableList<Condition> = mutableListOf(),
        arguments: MutableList<CommandArgument<*>> = mutableListOf()) : GenericCommand(arguments) {
    private val executions = mutableListOf<ExecutionInfo>()
    private val subcommands = mutableListOf<Command>()

    class ExecutionInfo(val run: Execution.() -> Unit)

    init {
        onlyIf { sender.hasPermission(permission) }.orElseError("You do not have the permission to run this command!")
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

        //possible fixes for the delegation issues, we can't pass a property, but it might not be needed
        infix fun <T> CommandArgument<T>.set(other: T) =
                setValue(this@Execution, other)

        operator fun <T> CommandArgument<T>.invoke() =
                getValue(this@Execution)

        operator fun <T> CommandArgument<T>.setValue(thisRef: Any?, property: KProperty<*>, value: T) =
                getValue(this@Execution)
    }

    /**
     * Called when the command should be executed
     */
    fun execute(sender: CommandSender, args: List<String>) {
        if (args.isNotEmpty() && subcommands.isNotEmpty())
            subcommands.firstOrNull { it.names.contains(args[0]) } //look for a sub-command matching the first argument
                    //first argument is the second item in the list since the first is the current command's name
                    ?.let {
                        it.execute(sender, args.drop(1))//execute it if found, removing this argument from the list
                        return //stop here if found
                    }

        if (subcommands.isNotEmpty() && executions.isEmpty()) {
            sender.error("Missing arguments, choose one of: ${subcommands.map { it.names[0] }}")
            return
        }
        //run this command's executions
        executions.forEach { info ->
            val execution = Execution(sender, args)

            conditions.forEach { it.check(execution) } //"register" all the conditions on the cueerrent Execution object

            //verify all the conditions have been met and arguments parsed correctly
            if (execution.conditionsMet() && arguments.all { it.verifyAndCheckMissing(execution) }) {
                info.run(execution)
                arguments.forEach { it.unregister(execution) }
            }
        }
    }

    internal fun addConditions(conditions: List<Condition>) = this.conditions.addAll(conditions)

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

    fun onlyIfSenderIsPlayer(): Condition =
            onlyIf { sender is Player }.orElse { sender.error("Only players can run this command") }

    //BUILDER FUNCTIONS
    fun onExecute(run: ExecutionExtension) = executions.add(ExecutionInfo(run))

    fun command(vararg names: String, init: Command.() -> Unit) =
            initTag(Command(names.toList(), "$permissionChain.${names[0]}", arguments = arguments), init, subcommands)

    /**
     * Group commands which share methods or variables together, so commands outside this scope can't see them
     */
    fun commandGroup(init: CommandGroup.() -> Unit) = CommandGroup(this, arguments).init()
}

class CommandGroup(
        val parent: Command,
        arguments: MutableList<CommandArgument<*>>) : GenericCommand(arguments) {
    private val sharedInit = mutableListOf<Command.() -> Unit>()

    fun command(vararg names: String, init: Command.() -> Unit) {
        val command = parent.command(names = *names, init = init) //* is for varargs
        for (runShared in sharedInit)
            command.runShared() //apply all our shared conditions
        command.addArguments(arguments)
    }

    /**
     * Will run on creation of all sub-commands in this CommandGroup. Useful for sharing conditions.
     *
     * If multiple shared blocks are created, all blocks declared above a command will be executed. Anything below
     * will not, so you can add additional conditions or run additional actions on specific commands.
     */
    fun shared(conditions: Command.() -> Unit) = sharedInit.add(conditions)
}
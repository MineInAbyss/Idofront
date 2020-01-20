package com.mineinabyss.idofront.commands

import com.mineinabyss.idofront.commands.Command.Condition
import com.mineinabyss.idofront.commands.Command.Execution
import com.mineinabyss.idofront.error
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

typealias ConditionLambda = Execution.() -> Boolean
typealias ExecutionExtension = Execution.() -> Unit

open class Command(
        val names: List<String>,
        private val permissionChain: String,
        permission: String = "$permissionChain.${names[0]}"
) : Tag() {
    private val executions = mutableListOf<ExecutionInfo>()
    private val subcommands = mutableListOf<Command>()
    private val conditions = mutableListOf<Condition>()
    private val arguments = mutableListOf<CommandArgument<*>>()

    class ExecutionInfo(val run: Execution.() -> Unit)

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
                    condition.check(this).also { if (!it) condition.fail(this) }
                }.isEmpty()
    }

    /**
     * Called when the command should be executed
     */
    fun execute(sender: CommandSender, args: List<String>) {
        subcommands.firstOrNull { it.names.contains(args[1]) } //look for a sub-command matching the first argument
                //first argument is the second item in the list since the first is the current command's name
                ?.execute(sender, args.subList(1, args.size)) //execute it if found, removing this argument from the list TODO not sure if args.size is the right size
                ?.let { return } //stop here if found

        //run this command's executions
        executions.forEach { info ->
            val execution = Execution(sender, args)

            conditions.forEach { it.check(execution) } //"register" all the conditions on the current Execution object

            if (arguments.all {
                        //if not enough arguments have been passed and a default was specified, we can use the default
                        (execution.args.size < it.order && it.default == null) ||
                                //verify that the argument can be parsed properly
                                it.verify(execution)
                    } && execution.conditionsMet()) { //verify all the conditions have been met
                info.run(execution)
                arguments.forEach { it.unregister(execution) }
            }
        }
    }

    internal fun addArgument(argument: CommandArgument<*>) = arguments.add(argument)

    internal fun addArguments(arguments: List<CommandArgument<*>>) = this.arguments.addAll(arguments)

    internal fun addConditions(conditions: List<Condition>) = this.conditions.addAll(conditions)

    //CONDITIONS
    inner class Condition(val check: ConditionLambda) {
        private val runOnFail = mutableListOf<ExecutionExtension>()

        fun orElse(run: ExecutionExtension) = runOnFail.add(run).let { this }

        internal fun fail(execution: Execution) = runOnFail.forEach { it.invoke(execution) }
    }

    fun onlyIf(condition: ConditionLambda): Condition = Condition(condition).also { conditions.add(it) }


    fun onlyIfSenderIsPlayer(): Condition =
            onlyIf { sender !is Player }.orElse { sender.error("Only players can run this command") }

    //BUILDER FUNCTIONS
    fun onExecute(run: ExecutionExtension) = executions.add(ExecutionInfo(run))

    fun command(vararg names: String, init: Command.() -> Unit) =
            initTag(Command(names.toList(), "$permissionChain.${names[0]}"), init, subcommands)
                    //arguments registered in the current command should be registered in child commands as well
                    .also { command ->
                        command.addArguments(arguments) //arguments are inherited by lower commands
                        command.addConditions(conditions) //same for conditions
                    }
//    fun command(permission: String, vararg names: String, init: Command.() -> Unit) =
//            initTag(Command(names.toList(), "$permissionChain.${names[0]}", permission), init, subcommands)

    /**
     * Group commands which share methods or variables together, so commands outside this scope can't see them
     */
    fun commandGroup(init: CommandGroup.() -> Unit) = CommandGroup(this, conditions.toList()).init()

    //extensions for our supported argument types so you don't have to pass `this` each time
    fun StringArgument(order: Int, name: String, default: String? = null) = StringArgument(this, order, name, default)

    fun IntArgument(order: Int, name: String, default: Int? = null) = IntArgument(this, order, name, default)
}

class CommandGroup(val parent: Command, val conditions: List<Condition>) : Tag() {
    private val sharedInit = mutableListOf<Command.() -> Unit>()

    fun command(vararg names: String, init: Command.() -> Unit) {
        val command = parent.command(names = *names, init = init) //* is for varargs
        for (runShared in sharedInit)
            command.runShared() //apply all our shared conditions
        command.addConditions(conditions)//adds the parent's conditions
    }

    /**
     * Will run on creation of all sub-commands in this CommandGroup. Useful for sharing conditions.
     *
     * If multiple shared blocks are created, all blocks declared above a command will be executed. Anything below
     * will not, so you can add additional conditions or run additional actions on specific commands.
     */
    fun shared(conditions: Command.() -> Unit) = sharedInit.add(conditions)

    //extensions for our supported argument types so you don't have to pass `parent` each time
    fun StringArgument(order: Int, name: String, default: String? = null) = StringArgument(parent, order, name, default)

    fun IntArgument(order: Int, name: String, default: Int? = null) = IntArgument(parent, order, name, default)
}
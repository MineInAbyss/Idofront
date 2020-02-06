package com.mineinabyss.idofront.commands

import com.mineinabyss.idofront.commands.Command.Execution
import com.mineinabyss.idofront.messaging.error
import org.bukkit.entity.Player
import kotlin.reflect.KProperty

/**
 * @param command A reference to the command that will be using this argument. Any sub-commands will automatically get a
 * reference to this argument internally.
 * @param order the order in which to read this argument. 1 indicates it is the first, etc...
 */
abstract class CommandArgument<T>(command: GenericCommand, order: Int, val name: String, val default: T? = null) {
    val order: Int = if (order < 1) error("Command argument $name was created with a negative order. Must start from 1!") else order - 1 //when creating the arguments, it makes sense for order to be 1 indexed, but internally we treat it as 0 indexed
    private val checks = mutableListOf<Execution.() -> Boolean>()
    private val runIfInvalid = mutableListOf<Execution.(KProperty<*>) -> Unit>()
    protected var missingMessage = "Please input the $name"
    protected var parseErrorMessage = "Could not parse the $name"
    private val parsedValues = mutableMapOf<Execution, T>()

    init {
        command.addArgument(this)
    }

    fun ensureChangedByPlayer()  {
        if(default == null)
            error("onlyIfChangedByPlayer check added to command argument $name even though it does not have a default value")
        ensure { sender is Player }
    }

    fun ensure(check: Execution.() -> Boolean) {
        checks.add(check)
    }

    fun getValue(execution: Execution): T {
//        if (execution !is Execution)
//            error("$name was accessed from $execution. Can only be run from a Command Execution")

        //parse the value once or get the already parsed value to avoid extra computation time
        //if a default value is set, but there were more arguments passed, we are guaranteed they passed the validation
        // and therefore those arguments should be used, and not the default. If those failed, they will show a fail
        // message at validation.
        return parsedValues.getOrPut(execution) {
            if (default != null && execution.args.size <= order) default
            else parse(execution)
        }
    }

    fun setValue(execution: Execution, value: T) {
//        if (execution !is Execution)
//            error("$name was set from $execution. Can only be run from a Command Execution")

        parsedValues[execution] = value
    }


    abstract fun parse(execution: Execution): T

    //TODO some better function names
    /**
     * Runs checks on an [Execution] and sends an error message to the sender when they fail.
     *
     * @param execution The execution to run checks on.
     * @return Whether all checks have passed and this argument can be parsed correctly.
     */
    protected abstract fun verify(execution: Execution): Boolean

    fun verifyAndCheckMissing(execution: Execution): Boolean {
        if (execution.args.size <= order) {
            if (default != null) { //if a default has been set, we don't parse anything and use the default
                return true
            }
            execution.sendMissingError()
            return false
        }
        if(!checks.all { it(execution) }) //TODO combine with Commands' system and make errors work
            return false
        //TODO verify should return the parsed value if it succeeds so we can add it to the map right away
        return verify(execution)
    }

    protected val Execution.arg
        get() = this.args[order]

    //if the default value is null we don't send an error message, since the default will be used anyways
    protected fun Execution.sendMissingError() =
            sendError("[$order] $missingMessage")

    protected fun Execution.sendParseError() =
            sendError("[$order] $parseErrorMessage")

    protected fun Execution.sendError(message: String) =
            sender.error(parseMessage(message))

    private fun Execution.parseMessage(message: String) =
            message.replace("%name%", name)
                    .apply { if (args.size > order) this.replace("%input%", args[order]) } //avoid index out of bounds

    /**
     * Run something if the argument wasn't passed properly
     *
     * @return This same [CommandArgument]
     */
    // TODO if we ever need more than just the KProperty passed, make it take a class here
    fun whenInvalid(run: Execution.(KProperty<*>) -> Unit) = runIfInvalid.add(run).let { this }

    fun withMissingMessage(message: String): CommandArgument<T> {
        missingMessage = message
        return this
    }

    fun withParseErrorMessage(message: String): CommandArgument<T> {
        parseErrorMessage = message
        return this
    }

    fun unregister(execution: Execution) = parsedValues.remove(execution)
}

open class StringArgument(command: GenericCommand, order: Int, name: String, default: String? = null) : CommandArgument<String>(command, order, name, default) {
    override fun parse(execution: Execution): String =
            execution.arg

    override fun verify(execution: Execution) = true
}

open class OptionArgument(command: GenericCommand, order: Int, name: String, val options: List<String> = listOf(), default: String? = null) : StringArgument(command, order, name, default) {
    init {
        parseErrorMessage = "$name needs to be one of: $options"
    }

    override fun verify(execution: Execution) =
            when {
                options.contains(execution.arg) -> true
                else -> {
                    execution.sendParseError()
                    false
                }
            }
}

open class BooleanArgument(command: GenericCommand, order: Int, name: String, default: Boolean? = null) : CommandArgument<Boolean>(command, order, name, default) {
    init {
        parseErrorMessage = "$name can only be true or false"
        missingMessage = "Please input whether $name is true or false"
    }

    override fun parse(execution: Execution): Boolean = execution.arg.toBoolean()

    override fun verify(execution: Execution) =
            when (execution.arg) {
                "true", "false" -> true
                else -> {
                    execution.sendParseError()
                    false
                }
            }
}

open class IntArgument constructor(command: GenericCommand, order: Int, name: String, default: Int? = null) : CommandArgument<Int>(command, order, name, default) {
    init {
        parseErrorMessage = "Please input a valid integer for the $name"
    }

    override fun parse(execution: Execution): Int = execution.arg.toInt()

    override fun verify(execution: Execution): Boolean =
            when {
                execution.arg.toIntOrNull() == null -> {
                    execution.sendParseError()
                    false
                }
                else -> true
            }
}
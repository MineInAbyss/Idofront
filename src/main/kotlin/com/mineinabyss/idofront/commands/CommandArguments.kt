package com.mineinabyss.idofront.commands

import com.mineinabyss.idofront.commands.Command.Execution
import com.mineinabyss.idofront.error
import kotlin.reflect.KProperty


abstract class CommandArgument<T>(command: Command, val order: Int, val name: String, val default: T? = null) {
    private val runIfInvalid = mutableListOf<Execution.(KProperty<*>) -> Unit>()
    protected var missingMessage: String = "Please input the $name"
    protected var parseErrorMessage: String = "Could not parse the $name"
    private val parsedValues = mutableMapOf<Execution, MutableMap<KProperty<*>, T>>()

    init {
        command.addArgument(this)
    }

    operator fun getValue(execution: Any?, property: KProperty<*>): T {
        if (execution !is Execution)
            error("${property.name} was accessed from $execution. Can only be run from a Command Execution")

        //parse the value once or get the already parsed value to avoid extra computation time
        return parsedValues.getOrPut(execution) { mutableMapOf() }.getOrElse(property) {
            //if a default value is set, but there were more arguments passed, we are guaranteed they passed the validation
            // and therefore those arguments should be used, and not the default. If those failed, they will show a fail
            // message at validation.
            if (default != null && execution.args.size < order) default
            else parse(execution, property)
        }
    }

    operator fun setValue(execution: Any?, property: KProperty<*>, value: T) {
        if (execution !is Execution)
            error("${property.name} was set from $execution. Can only be run from a Command Execution")

        parsedValues.getOrPut(execution) { mutableMapOf() }[property] = value
    }


    abstract fun parse(execution: Execution, property: KProperty<*>): T

    abstract fun verify(execution: Execution): Boolean

    protected fun sendMissingError(execution: Execution) =
            //if the default value is null we don't send an error message, since the default will be used anyways
            execution.sender.error(execution.parseMessage(missingMessage))

    protected fun sendParseError(execution: Execution) =
            execution.sender.error(execution.parseMessage(parseErrorMessage))

    private fun Execution.parseMessage(message: String) =
            message.replace("%name%", name)
                    .replace("%input%", args[order])

    /**
     * Run something if the argument wasn't passed properly
     *
     * @return This same [CommandArgument]
     */
    // TODO if we ever need more than just the KProperty passed, make it take an class here
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

class StringArgument(command: Command, order: Int, name: String, default: String? = null) : CommandArgument<String>(command, order, name, default) {
    override fun parse(execution: Execution, property: KProperty<*>): String =
            execution.args[order]

    override fun verify(execution: Execution) =
            (execution.args.size >= order).also {
                if (it) sendParseError(execution)
            }
}

class IntArgument(command: Command, order: Int, name: String, default: Int? = null) : CommandArgument<Int>(command, order, name, default) {
    init {
        parseErrorMessage = "Please input a valid integer for the $name"
    }

    override fun parse(execution: Execution, property: KProperty<*>): Int =
            execution.args[order].toInt()

    override fun verify(execution: Execution): Boolean =
            when {
                execution.args.size >= order -> {
                    sendMissingError(execution)
                    false
                }
                execution.args[order].toIntOrNull() == null -> {
                    sendParseError(execution)
                    false
                }
                else -> true
            }
}
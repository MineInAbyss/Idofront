package com.mineinabyss.idofront.commands.arguments

import com.mineinabyss.idofront.commands.Command.Execution
import com.mineinabyss.idofront.commands.GenericCommand
import com.mineinabyss.idofront.messaging.color
import com.mineinabyss.idofront.messaging.error
import org.bukkit.entity.Player
import kotlin.reflect.KProperty

typealias CmdInit <T> = T.() -> Unit

//fun <T, R : CommandArgument<T>> (R.() -> Unit)?.cast(): CommandArgument<T>.() -> Unit =
//        this as CommandArgument<T>.() -> Unit

fun <T, R : T> CmdInit<R>?.cast(): CmdInit<T>? {
    return (this ?: return null) as CmdInit<T>
}

/**
 * @property order the order in which to read this argument. 1 indicates it is the first, etc...
 */
abstract class CommandArgument<T>(val name: String, init: (CommandArgument<T>.() -> Unit)?) {
    var default: T? = null //TODO make this work with null defaults
    var order: Int = -1
        set(value) {
            //when creating the arguments, it makes sense for order to be 1 indexed, but internally we treat it as 0 indexed
            field = if (value < 0) error("Command argument $name was created with a negative order. Must start from 1!") else value
        }
    private val checks = mutableListOf<GenericCommand.() -> Boolean>()
    private val runIfInvalid = mutableListOf<Execution.(KProperty<*>) -> Unit>()
    protected var missingMessage = "Please input the $name"
    var parseErrorMessage: (passed: String) -> String = { passed -> "Could not parse $passed for the $name" }
    private var parsedValue: T? = null

    init {
        if (init != null) init()
    }

    fun ensureChangedByPlayer() { //TODO better name
        if (default == null)
            error("onlyIfChangedByPlayer check added to command argument $name even though it does not have a default value")
        ensure { sender is Player }
    }

    fun ensure(check: GenericCommand.() -> Boolean) {
        checks.add(check)
    }

    /**
     * Parses the value once or get the already parsed value to avoid extra computation time.
     *
     * If a default value is set, but there were more arguments passed, we are guaranteed they passed the validation
     * and therefore those arguments should be used, and not the default. If those failed, they will show a fail
     * message at validation.
     */
    fun getValue(command: GenericCommand): T {
        if (!command.hasThisArgument()) return default ?: parse(command)
        return parsedValue ?: parse(command).also { parsedValue = it }
    }

    fun setValue(command: GenericCommand, value: T) {
        parsedValue = value
    }

    abstract fun parse(command: GenericCommand): T

    //TODO some better function names
    /**
     * Runs checks on an [Execution] and sends an error message to the sender when they fail.
     *
     * @param command The execution to run checks on.
     * @return Whether all checks have passed and this argument can be parsed correctly.
     */
    protected abstract fun verify(command: GenericCommand): Boolean

    fun verifyAndCheckMissing(command: GenericCommand): Boolean {
        if (!command.hasThisArgument()) {
            //if a default has been set, we don't parse anything and use the default
            if (default != null) return true
            command.sendMissingError()
            return false
        }
        if (!checks.all { it(command) }) //TODO combine with Commands' system and make errors work
            return false
        //TODO verify should return the parsed value if it succeeds so we can add it to the map right away
        return verify(command)
    }

    val GenericCommand.arg
        get() = this.argumentParser[order]

    private fun GenericCommand.hasThisArgument() = argumentParser.size > order + depth

    //if the default value is null we don't send an error message, since the default will be used anyways
    protected fun GenericCommand.sendMissingError() =
            sender.error("&7&l[&4&l!&7&l]&c $missingMessage".color())

    //TODO make this run invalid things
    protected fun GenericCommand.sendParseError() =
            sender.error("&7&l[&4&l!&7&l]&c ${parseErrorMessage(arg)}".color())

    /**
     * Run something if the argument wasn't passed properly
     *
     * @return This same [CommandArgument]
     */
    fun whenInvalid(run: Execution.(KProperty<*>) -> Unit) = runIfInvalid.add(run).let { this }
}
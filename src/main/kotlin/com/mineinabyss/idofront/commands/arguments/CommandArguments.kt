package com.mineinabyss.idofront.commands.arguments

import com.mineinabyss.idofront.commands.Command.Execution
import com.mineinabyss.idofront.commands.GenericCommand
import com.mineinabyss.idofront.messaging.color
import com.mineinabyss.idofront.messaging.error
import org.bukkit.entity.Player
import kotlin.reflect.KProperty

typealias CmdInit <T> = T.() -> Unit

fun <T, R : T> CmdInit<R>?.cast(): CmdInit<T>? {
    return (this ?: return null) as CmdInit<T>
}

class CommandArgumentBuilder<T>(
        private val parseBy: GenericCommand.(String) -> T,
        val verify: (CommandArgument<T>.() -> Boolean) = {
            runCatching { this.parseBy() }.isSuccess
        },
        val init: (CommandArgument<T>.() -> Unit)?

) {
    operator fun provideDelegate(thisRef: GenericCommand, prop: KProperty<*>): CommandArgument<T> {
        val argument = CommandArgument(thisRef, prop.name, parseBy, verify, init)
        thisRef.addArgument(argument)

        if (init != null) init()
        return
    }
}

/**
 * @param verify Runs checks on an [Execution] and sends an error message to the sender when they fail.
 *
 * @property order the order in which to read this argument. 1 indicates it is the first, etc...
 */
class CommandArgument<T>(
        val command: GenericCommand,
        val name: String,
        val parseBy: CommandArgument<T>.() -> T,
        val verify: (CommandArgument<T>.() -> Boolean)
) {
    val default: T? = null //TODO make this work with null defaults
    val order: Int = command.argumentParser.size + 1
    val passed: String get() = command.run { argumentParser[order] }

    private val argumentWasPassed get() = command.argumentParser.size > order + command.depth
    private var parsedValue: T? = null

    //ARGUMENT DELEGATION
    /**
     * Parse the value once or get the already parsed value to avoid extra computation time.
     *
     * If a default value is set, but there were more arguments passed, we are guaranteed they passed the validation
     * and therefore those arguments should be used, and not the default. If those failed, they will show a fail
     * message at validation.
     */
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (!argumentWasPassed) return default ?: command.parseBy(passed)
        return parsedValue ?: command.parseBy(passed).also { parsedValue = it }
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        parsedValue = value
    }

    fun verifyAndCheckMissing(): Boolean {
        if (!argumentWasPassed) {
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


    //CUSTOMIZATION
    private val checks = mutableListOf<GenericCommand.() -> Boolean>()
    private val runIfInvalid = mutableListOf<Execution.(KProperty<*>) -> Unit>()

    fun ensureChangedByPlayer() { //TODO better name
        if (default == null)
            error("onlyIfChangedByPlayer check added to command argument $name even though it does not have a default value")
        ensure { sender is Player }
    }

    fun ensure(check: GenericCommand.() -> Boolean) {
        checks.add(check)
    }

    /** Run something if the argument wasn't passed properly */
    fun whenInvalid(run: Execution.(KProperty<*>) -> Unit) = runIfInvalid.add(run).let { this }

    var missingMessage = "Please input the $name"
    var parseErrorMessage: CommandArgument<T>.() -> String = { "Could not parse $passed for the $name" }

    //if the default value is null we don't send an error message, since the default will be used anyways
    private fun GenericCommand.sendMissingError() =
            sender.error("$ERROR $missingMessage".color())

    //TODO make this run invalid things
    private fun GenericCommand.sendParseError() =
            sender.error("$ERROR ${parseErrorMessage()}".color())
}

//TODO this should probably just be printed through sender.error
private const val ERROR = "&7&l[&4&l!&7&l]&c"
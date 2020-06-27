package com.mineinabyss.idofront.commands.arguments

import com.mineinabyss.idofront.commands.Command.Execution
import com.mineinabyss.idofront.commands.ExecutableCommand
import com.mineinabyss.idofront.messaging.color
import com.mineinabyss.idofront.messaging.error
import org.bukkit.entity.Player
import kotlin.reflect.KProperty

/**
 * @property verify Runs checks on an [Execution] and sends an error message to the sender when they fail.
 * @property order the order in which to read this argument. 1 indicates it is the first, etc...
 */
class CommandArgument<T>(
        command: ExecutableCommand,
        val name: String
) {
    var command = command; private set
    private var parseBy: CommandArgument<T>.() -> T = {
        error("No way to parse arguments for argument '$name'")
    }
    private var verify: (CommandArgument<T>.() -> Boolean) = {
        runCatching { this.parseBy() }.isSuccess
    }

    //TODO better names would be nice
    fun parseBy(parse: CommandArgument<T>.() -> T) {
        parseBy = parse
    }

    fun verify(verify: CommandArgument<T>.() -> Boolean) {
        this.verify = verify
    }

    var default: T? = null //TODO make this work with null defaults
    val order: Int = command.argumentParser.argumentsSize
    val passed: String get() = command.run { argumentParser[this@CommandArgument] }

    private val argumentWasPassed get() = command.argumentParser.size > order
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
        if (!argumentWasPassed) return default ?: parseBy()
        return parsedValue ?: parseBy().also { parsedValue = it }
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        parsedValue = value
    }

    fun verifyAndCheckMissing(accessedCommand: ExecutableCommand): Boolean {
        //TODO this is hopefully just a temporary fix so when a subcommand executes, this argument knows that's the reference
        // it should be looking at. Technically we could do this without storing a reference in this object, but it's
        // a bit more annoying to deal with. We can't define the right command right away since we don't know which subcommand
        // will be accessing properties until the subcommand finally accesses something.
        this.command = accessedCommand

        if (!argumentWasPassed) { //TODO not verifying correctly
            //if a default has been set, we don't parse anything and use the default
            if (default != null) return true
            sendMissingError()
            return false
        }
        if (!checks.all { it(this.command) }) //TODO combine with Commands' system and make errors work
            return false
        //TODO verify should return the parsed value if it succeeds so we can add it to the map right away
        return verify().also { if (!it) sendParseError() }
    }

    //CUSTOMIZATION
    private val checks = mutableListOf<ExecutableCommand.() -> Boolean>()
    private val runIfInvalid = mutableListOf<Execution.(KProperty<*>) -> Unit>()

    fun ensureChangedByPlayer() { //TODO better name
        if (default == null)
            error("ensureChangedByPlayer check added to command argument $name even though it does not have a default value")
        ensure { sender is Player }
    }

    fun ensure(check: ExecutableCommand.() -> Boolean) {
        checks.add(check)
    }

    /** Run something if the argument wasn't passed properly */
    fun whenInvalid(run: Execution.(KProperty<*>) -> Unit) = runIfInvalid.add(run).let { this }

    var missingMessage: CommandArgument<T>.() -> String = { "Please input the $name" }
    var parseErrorMessage: CommandArgument<T>.() -> String = { "Could not parse $passed for the $name" }

    //if the default value is null we don't send an error message, since the default will be used anyways
    private fun sendMissingError() =
            command.sender.error("$ERROR ${missingMessage()}".color())

    private fun sendParseError() =
            command.sender.error("$ERROR ${parseErrorMessage()}".color())

    internal fun initWith(init: (CommandArgument<T>.() -> Unit)?) {
        init?.invoke(this)
    }
}

//TODO this should probably just be printed through sender.error
private const val ERROR = "&7&l[&4&l!&7&l]&c"
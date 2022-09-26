package com.mineinabyss.idofront.commands.arguments

import com.mineinabyss.idofront.commands.BaseCommand
import com.mineinabyss.idofront.commands.execution.Action
import com.mineinabyss.idofront.commands.execution.stopCommand
import com.mineinabyss.idofront.messaging.error
import kotlin.reflect.KProperty

/**
 * An argument that is to be passed to a [command]
 *
 * @param command The command this argument is associated with.
 * @param name The name of this argument, which will be used in chat messages.
 * @param T The type this argument will be parsed into.
 *
 * @property parseBy Describes how to parse the command argument into [T].
 * @property verify Describes how to check whether parsing should proceed. If false, will send an error to the sender.
 * If true, will attempt to parse via [parseBy].
 * @property order the order in which to read this argument. 1 indicates it is the first, etc...
 */
//TODO introduce conditional default arguments (e.x. if sender is player, default location argument to their location)
class CommandArgument<T>(
    command: BaseCommand,
    var name: String
) {
    var command = command; private set
    private var parseBy: CommandArgument<T>.() -> T = {
        error("No way to parse arguments for argument '$name'")
    }
    private var verify: (CommandArgument<T>.() -> Boolean) = {
        runCatching { this.parseBy() }.isSuccess
    }

    /** Describes how to parse the command argument into [T].  */
    fun parseBy(parse: CommandArgument<T>.() -> T) {
        parseBy = parse
    }

    /** Describes how to check whether parsing should proceed. If false, will send an error to the sender. If true, will attempt
     * to parse via [parseBy]*/
    fun verify(verify: CommandArgument<T>.() -> Boolean) {
        this.verify = verify
    }

    var default: T? = null //TODO make this work with null defaults
    val order: Int = command.arguments.size
    val passed: String get() = command[this@CommandArgument]
    val argumentWasPassed get() = command.strings.size > order
    private var parsedValue: T? = null
    var parsedSuccessfully: Boolean? = null
        private set

    fun verifyAndCheckMissing(accessedCommand: BaseCommand): Boolean {
        if (parsedSuccessfully == true) return true

        //TODO when a subcommand executes, this argument needs to know that's the reference it should be looking at.
        // We can't define the right command right away since we don't know which subcommand will be accessing
        // properties until the subcommand finally accesses something. It would be preferable to avoid mutability and
        // avoid having a reference to the command outside of this scope.
        command = accessedCommand
        if (!accessedCommand.argumentsWereSent) {
            accessedCommand.stopCommand {
                accessedCommand.sendCommandDescription(showAliases = false, showDesc = false)
            }
        }
        if (!argumentWasPassed) {
            //if argument wasn't passed but a default is set, don't send an error message since the default will be used
            if (default != null) return true
            accessedCommand.stopCommand {
                accessedCommand.sendCommandDescription(showAliases = false, showDesc = false)
                sender.error(missingMessage())
            }
        }

        //TODO verify should return the parsed value if it succeeds so we can add it to the map right away
        if (!verify()) {
            parsedSuccessfully = false
            accessedCommand.stopCommand {
                accessedCommand.sendCommandDescription(showAliases = false, showDesc = false)
                sender.error(parseErrorMessage())
            }
        }
        parsedSuccessfully = true
        return true
    }

    //CUSTOMIZATION
    private val checks = mutableListOf<BaseCommand.() -> Boolean>()
    private val runIfInvalid = mutableListOf<Action.(KProperty<*>) -> Unit>()

    /** Run something if the argument wasn't passed properly */
    fun whenInvalid(run: Action.(KProperty<*>) -> Unit) = runIfInvalid.add(run).let { this }

    var missingMessage: CommandArgument<T>.() -> String = { "Please input the $name" }
    var parseErrorMessage: CommandArgument<T>.() -> String = { "Could not parse $passed for the $name" }


    internal fun initWith(init: (CommandArgument<T>.() -> Unit)?) {
        init?.invoke(this)
    }

    //ARGUMENT DELEGATION
    /**
     * Parse the value once or get the already parsed value to avoid extra computation time.
     *
     * If a default value is set, but there were more arguments passed, we are guaranteed they passed the validation
     * and therefore those arguments should be used, and not the default. If those failed, they will show a fail
     * message at validation.
     */
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        command.argumentsMet()
        if (!argumentWasPassed) return default ?: parseBy()
        return parsedValue ?: parseBy().also { parsedValue = it }
    }

    /** Update [parsedValue] to [value] */
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        parsedValue = value
    }
}

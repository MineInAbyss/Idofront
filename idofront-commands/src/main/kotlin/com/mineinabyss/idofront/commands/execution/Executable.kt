package com.mineinabyss.idofront.commands.execution

interface Executable {
    var executedCommand: Boolean

    /** Called when the command should be executed. */
    fun canExecute(): Boolean

    /** Execute an [Action] on the command. */
    fun <E : Action> E.execute(run: E.() -> Unit)

    /** Send the command's sender a nicely-formatted message describing this command and its subcommands. */
    fun sendCommandDescription(showAliases: Boolean = true, showArgs: Boolean = true, showDesc: Boolean = true)

    /** An action to execute when the command is run and all conditions and argument requirements are met. */
    fun action(run: Action.() -> Unit)
}

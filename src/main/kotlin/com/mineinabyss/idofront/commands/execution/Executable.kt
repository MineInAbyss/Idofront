package com.mineinabyss.idofront.commands.execution

interface Executable {
    var executedCommand: Boolean

    /** Called when the command should be executed*/
    fun canExecute(): Boolean

    fun <E : Execution> execute(run: E.() -> Unit, execution: E)

    fun onExecute(run: Execution.() -> Unit)

    fun <E : Execution> E.onExecute(run: E.() -> Unit) = execute(run, this)

    fun sendCommandDescription()
}

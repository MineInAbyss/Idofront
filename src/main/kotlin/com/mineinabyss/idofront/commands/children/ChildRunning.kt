package com.mineinabyss.idofront.commands.children

import com.mineinabyss.idofront.commands.BaseCommand
import com.mineinabyss.idofront.commands.Command

interface ChildRunning {
    val subcommands: List<BaseCommand>

    /** Runs a child command */
    fun runChildCommandOn(command: BaseCommand, subcommand: Command, init: Command.() -> Unit): Command?

    fun addCommand(command: BaseCommand)
}

/** @see ChildRunning.runChildCommandOn */
fun BaseCommand.runChildCommand(subcommand: Command, init: Command.() -> Unit) =
    runChildCommandOn(this, subcommand, init)

package com.mineinabyss.idofront.commands.children

import com.mineinabyss.idofront.commands.BaseCommand
import com.mineinabyss.idofront.commands.Command

class ChildManager : ChildRunning {
    override val subcommands: List<BaseCommand>
        get() = _subcommands.toList()
    private val _subcommands = mutableListOf<BaseCommand>()

    override fun addCommand(command: BaseCommand) {
        _subcommands += command
    }

    override fun runChildCommandOn(command: BaseCommand, subcommand: Command, init: Command.() -> Unit): BaseCommand {
        _subcommands += subcommand

        with(command) {
            //if there are extra arguments and sub-commands exist, we first try to match them to any sub-commands
            if (argumentsWereSent && subcommand.names.contains(firstArgument)) {
                applySharedTo(subcommand)
                subcommand.runWith(init)
                this.executedCommand = true
            }
        }
        return subcommand
    }
}
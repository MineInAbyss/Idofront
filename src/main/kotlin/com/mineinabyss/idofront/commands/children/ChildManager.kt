package com.mineinabyss.idofront.commands.children

import com.mineinabyss.idofront.commands.BaseCommand
import com.mineinabyss.idofront.commands.Command
import com.mineinabyss.idofront.commands.CommandCreation

class ChildManager : ChildContaining {
    override val subcommands: List<CommandCreation>
        get() = _subcommands.toList()
    private val _subcommands = mutableListOf<CommandCreation>()

    override val sharedInit = mutableListOf<Command.() -> Unit>()

    override fun shared(conditions: Command.() -> Unit) {
        sharedInit.add(conditions)
    }

    override fun runChildCommandOn(command: BaseCommand, subcommand: CommandCreation): CommandCreation {
        _subcommands += subcommand

        with(command) {
            //if there are extra arguments and sub-commands exist, we first try to match them to any sub-commands
            if (argumentsWereSent && subcommand.names.contains(firstArgument)) {
                subcommand.newInstance(sender, strings.drop(1))
                executedCommand = true
            }
        }
        return subcommand
    }
}
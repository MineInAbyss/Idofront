package com.mineinabyss.idofront.commands

import com.mineinabyss.idofront.commands.arguments.Argumentable
import com.mineinabyss.idofront.commands.children.ChildContaining
import com.mineinabyss.idofront.commands.execution.Executable
import com.mineinabyss.idofront.commands.permissions.Permissionable
import com.mineinabyss.idofront.commands.sender.Sendable

/**
 * Command groups limit arguments defined inside of them to only the commands in the group.
 *
 * @property argumentParser A copy of the argumentParser is made so we aren't forced to pass the arguments in the group
 * for commands outside the group.
 */
class CommandGroup(
        private val parent: BaseCommand
) : BaseCommand,
        Argumentable by parent.childGroupParser(),
        ChildContaining by parent,
        Permissionable by parent,
        Executable by parent,
        Sendable by parent
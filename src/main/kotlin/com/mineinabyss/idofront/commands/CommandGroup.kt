package com.mineinabyss.idofront.commands

import com.mineinabyss.idofront.commands.arguments.Argumentable
import com.mineinabyss.idofront.commands.children.ChildRunning
import com.mineinabyss.idofront.commands.children.ChildSharing
import com.mineinabyss.idofront.commands.children.ChildSharingManager
import com.mineinabyss.idofront.commands.execution.Executable
import com.mineinabyss.idofront.commands.naming.Nameable
import com.mineinabyss.idofront.commands.permissions.Permissionable
import com.mineinabyss.idofront.commands.sender.Sendable

/**
 * Command groups limit arguments defined inside of them to only the commands in the group.
 *
 * @param parent The parent of this command group, to which most interface implementations will be delegated.
 */
class CommandGroup(
        private val parent: BaseCommand
) : BaseCommand,
        Argumentable by parent.childGroupParser(),
        ChildRunning by parent,
        ChildSharing by ChildSharingManager(),
        Executable by parent,
        Nameable by parent,
        Permissionable by parent,
        Sendable by parent
package com.mineinabyss.idofront.commands.children

import com.mineinabyss.idofront.commands.Command

class ChildSharingManager : ChildSharing {
    override val sharedInit get() = _sharedInit.toList()
    private val _sharedInit = mutableListOf<Command.() -> Unit>()

    override fun shared(block: Command.() -> Unit) {
        _sharedInit += block
    }
}
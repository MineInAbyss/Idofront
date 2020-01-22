@file:JvmName("CommandAPI")

package com.mineinabyss.idofront.commands

object CommandAPI {
    fun registerCommands(commands: CommandHolder) {
        commandRegisterer.addCommands(commands)
    }
}
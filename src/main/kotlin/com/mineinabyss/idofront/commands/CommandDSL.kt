package com.mineinabyss.idofront.commands

import com.mineinabyss.idofront.commands.Command.*
import com.mineinabyss.idofront.messaging.error
import org.bukkit.entity.Player


fun Command.onlyIfSenderIsPlayer() =
        onlyIf { sender is Player }.orElse { sender.error("Only players can run this command") }


fun Command.onExecuteByPlayer(run: PlayerExecution.() -> Unit) {
    onlyIfSenderIsPlayer()
    addExecution(ExecutionInfo(run as Execution.() -> Unit, { sender, args -> PlayerExecution(sender, args) }))
}
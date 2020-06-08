package com.mineinabyss.idofront.commands

import com.mineinabyss.idofront.commands.Command.*
import com.mineinabyss.idofront.messaging.error
import org.bukkit.entity.Player


fun Command.onlyIfSenderIsPlayer() =
        onlyIf { sender is Player }.orElse { sender.error("Only players can run this command") }


@Suppress("UNCHECKED_CAST")
fun Command.onExecuteByPlayer(run: PlayerExecution.() -> Unit) {
    onlyIfSenderIsPlayer() //TODO this needs to be specific to the execution
    execute(ExecutionInfo(run as Execution.() -> Unit, { _, _ -> PlayerExecution() }))
}
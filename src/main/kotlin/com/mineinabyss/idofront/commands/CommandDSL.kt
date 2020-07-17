package com.mineinabyss.idofront.commands

import com.mineinabyss.idofront.commands.execution.PlayerExecution
import com.mineinabyss.idofront.commands.execution.stopCommand
import org.bukkit.entity.Player


fun Command.ensureSenderIsPlayer() {
    if (sender !is Player) {
        stopCommand("Only players can run this command")
    }
}

@Suppress("UNCHECKED_CAST")
fun Command.onExecuteByPlayer(run: PlayerExecution.() -> Unit) {
    ensureSenderIsPlayer()
    PlayerExecution(this).onExecute(run)
}
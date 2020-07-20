package com.mineinabyss.idofront.commands.extensions.actions

import com.mineinabyss.idofront.commands.BaseCommand
import com.mineinabyss.idofront.commands.Command
import com.mineinabyss.idofront.commands.execution.Action
import com.mineinabyss.idofront.commands.execution.stopCommand
import org.bukkit.entity.Player


/**
 * An action run by a player.
 */
class PlayerAction(command: BaseCommand) : Action(command) {
    val player = sender as Player
}

fun Command.ensureSenderIsPlayer() {
    if (sender !is Player) {
        stopCommand("Only players can run this command")
    }
}

@Suppress("UNCHECKED_CAST")
fun Command.playerAction(run: PlayerAction.() -> Unit) {
    customAction(run) {
        ensureSenderIsPlayer()
        PlayerAction(this)
    }
}

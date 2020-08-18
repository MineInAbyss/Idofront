package com.mineinabyss.idofront.commands.extensions.actions

import com.mineinabyss.idofront.commands.BaseCommand
import com.mineinabyss.idofront.commands.Command
import com.mineinabyss.idofront.commands.execution.Action
import com.mineinabyss.idofront.commands.execution.stopCommand
import org.bukkit.Bukkit
import org.bukkit.entity.Player


/**
 * An action run by a player.
 */
class PlayerAction(command: BaseCommand, player: Player? = null) : Action(command) {
    val player: Player = player ?: sender as Player
}

fun BaseCommand.ensureSenderIsPlayer() {
    if (sender !is Player) {
        stopCommand("Only players can run this command")
    }
}

@Suppress("UNCHECKED_CAST")
fun Command.playerAction(otherPermission: String = "other", run: PlayerAction.() -> Unit) {
    customAction(run) {
        if (strings.size > arguments.size && permissions.any { sender.hasPermission("$it.$otherPermission") }) {
            val playerName = strings[arguments.size]
            return@customAction PlayerAction(
                    this,
                    Bukkit.getPlayer(playerName) ?: stopCommand("$playerName is not online")
            )
        }
        ensureSenderIsPlayer()
        PlayerAction(this)
    }
}

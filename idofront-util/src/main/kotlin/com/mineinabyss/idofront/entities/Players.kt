package com.mineinabyss.idofront.entities

import io.papermc.paper.adventure.PaperAdventure
import net.kyori.adventure.text.Component
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.craftbukkit.inventory.CraftContainer
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.InventoryView
import java.util.*

/**
 * Whether the player in this [PlayerInteractEvent] left clicked.
 */
val PlayerInteractEvent.leftClicked get() = action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK

/**
 * Whether the player in this [PlayerInteractEvent] right clicked.
 * TODO this event doesn't send out a packet when right clicking air with an empty hand
 */
val PlayerInteractEvent.rightClicked get() = action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK

fun UUID.toPlayer() = Bukkit.getPlayer(this)
fun UUID.toOfflinePlayer() = Bukkit.getOfflinePlayer(this)

fun InventoryView.title(title: Component) {
    val serverPlayer = (player as CraftPlayer).handle
    val containerId = serverPlayer.containerMenu.containerId
    val windowType = CraftContainer.getNotchInventoryType(topInventory)
    serverPlayer.connection.send(ClientboundOpenScreenPacket(containerId, windowType, PaperAdventure.asVanilla(title)))
    (player as Player).updateInventory()
}

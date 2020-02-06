package com.mineinabyss.idofront.entities

import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

/**
 * Whether the player in this [PlayerInteractEvent] left clicked.
 */
val PlayerInteractEvent.leftClicked get() = action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK

/**
 * Whether the player in this [PlayerInteractEvent] right clicked.
 * TODO this event doesn't send out a packet when right clicking air with an empty hand
 */
val PlayerInteractEvent.rightClicked get() = action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK

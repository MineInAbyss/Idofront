package com.mineinabyss.idofront.destructure

import org.bukkit.event.entity.PlayerLeashEntityEvent
import org.bukkit.event.player.*

operator fun PlayerExpChangeEvent.component2() = amount

operator fun PlayerMoveEvent.component2() = from
operator fun PlayerMoveEvent.component3() = to

operator fun PlayerItemHeldEvent.component2() = previousSlot
operator fun PlayerItemHeldEvent.component3() = newSlot

operator fun PlayerEvent.component1() = player

operator fun PlayerLeashEntityEvent.component1() = player
operator fun PlayerLeashEntityEvent.component2() = entity
operator fun PlayerLeashEntityEvent.component3() = leashHolder

operator fun PlayerTeleportEvent.component4() = cause
package com.mineinabyss.idofront.destructure

import org.bukkit.event.player.PlayerExpChangeEvent
import org.bukkit.event.player.PlayerMoveEvent

operator fun PlayerExpChangeEvent.component2() = amount

operator fun PlayerMoveEvent.component2() = from
operator fun PlayerMoveEvent.component3() = to
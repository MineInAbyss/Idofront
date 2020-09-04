package com.mineinabyss.idofront.destructure

import org.bukkit.event.vehicle.VehicleEvent
import org.bukkit.event.vehicle.VehicleMoveEvent


operator fun VehicleMoveEvent.component2() = from
operator fun VehicleMoveEvent.component3() = to
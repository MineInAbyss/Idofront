/**
 * This file covers most of the direct subclasses of Bukkit's Event class
 * As a general rule, try and follow this list for priority:
 * - The single player involved in the event
 * - The thing being acted upon
 * - The thing doing the action
 * - Additional information about the event
 */
package com.mineinabyss.idofront.destructure

import org.bukkit.entity.HumanEntity
import org.bukkit.event.block.BlockEvent
import org.bukkit.event.entity.EntityEvent
import org.bukkit.event.entity.PlayerLeashEntityEvent
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.inventory.InventoryPickupItemEvent
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.server.TabCompleteEvent
import org.bukkit.event.vehicle.VehicleEvent
import org.bukkit.event.weather.WeatherEvent
import org.bukkit.event.world.WorldEvent

operator fun BlockEvent.component1() = block

operator fun EntityEvent.component1() = entity

operator fun InventoryEvent.component1() = inventory
operator fun InventoryEvent.component2(): MutableList<HumanEntity> = viewers
operator fun InventoryEvent.component3() = view

operator fun InventoryMoveItemEvent.component1() = item
operator fun InventoryMoveItemEvent.component2() = source
operator fun InventoryMoveItemEvent.component3() = destination

operator fun InventoryPickupItemEvent.component1() = item
operator fun InventoryPickupItemEvent.component2() = inventory

operator fun TabCompleteEvent.component1() = sender
operator fun TabCompleteEvent.component2(): List<String> = completions
operator fun TabCompleteEvent.component3() = buffer

operator fun VehicleEvent.component1() = vehicle

operator fun WeatherEvent.component1() = world

operator fun WorldEvent.component1() = world

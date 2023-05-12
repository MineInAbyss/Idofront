@file:Suppress("NOTHING_TO_INLINE")

package com.mineinabyss.idofront.nms.aliases

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.level.Level
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld
import org.bukkit.craftbukkit.v1_19_R3.entity.*
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftInventoryPlayer
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack
import org.bukkit.entity.*
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.entity.Entity as BukkitEntity

typealias BukkitWorld = org.bukkit.World
typealias NMSWorld = Level
typealias NMSWorldServer = ServerLevel

inline fun BukkitWorld.toNMS(): NMSWorldServer = (this as CraftWorld).handle
inline fun NMSWorld.toBukkit(): BukkitWorld = this.world


//common conversions
/** Converts a Bukkit entity to an NMS entity */
inline fun BukkitEntity.toNMS(): NMSEntity = (this as CraftEntity).handle
inline fun LivingEntity.toNMS(): NMSLivingEntity = (this as CraftLivingEntity).handle
inline fun Mob.toNMS(): NMSMob = (this as CraftMob).handle
inline fun Creature.toNMS(): NMSPathfinderMob = (this as CraftCreature).handle
inline fun Player.toNMS(): NMSPlayer = (this as CraftPlayer).handle
inline fun Snowball.toNMS(): NMSSnowball = (this as CraftSnowball).handle

inline fun NMSEntity.toBukkit() = bukkitEntity as BukkitEntity
inline fun NMSLivingEntity.toBukkit() = bukkitEntity as LivingEntity
inline fun NMSMob.toBukkit() = bukkitEntity as Mob
inline fun NMSPathfinderMob.toBukkit() = bukkitEntity as Creature
inline fun NMSPlayer.toBukkit() = bukkitEntity as Player
inline fun NMSSnowball.toBukkit() = bukkitEntity as Snowball

/** Converts to an NMS entity casted to a specified type */
@Suppress("UNCHECKED_CAST")
@JvmName("toNMSWithCast")
inline fun <T : NMSEntity> BukkitEntity.toNMS(): T = (this as CraftEntity).handle as T

typealias NMSPlayerInventory = Inventory
typealias NMSItemStack = net.minecraft.world.item.ItemStack

fun PlayerInventory.toNMS(): NMSPlayerInventory = (this as CraftInventoryPlayer).inventory
fun ItemStack.toNMS(): NMSItemStack? = (this as? CraftItemStack)?.handle

fun NMSPlayerInventory.toBukkit(): PlayerInventory = CraftInventoryPlayer(this)
fun NMSItemStack.toBukkit(): ItemStack = CraftItemStack.asCraftMirror(this)

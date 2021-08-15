package com.mineinabyss.idofront.nms

import com.mineinabyss.idofront.nms.aliases.NMSEntityType
import com.mineinabyss.idofront.nms.aliases.toBukkit
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.typealiases.BukkitEntity
import net.minecraft.core.BlockPosition
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.chat.IChatBaseComponent
import net.minecraft.world.entity.EnumMobSpawn
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason

/**
 * Spawns entity at specified Location
 *
 * Originally from [paper forums](https://papermc.io/forums/t/register-and-spawn-a-custom-entity-on-1-13-x/293)
 *
 * @param type The type of entity to spawn *
 * @return Reference to the spawned bukkit Entity
 */
fun Location.spawnEntity(
    type: NMSEntityType<*>,
    nbtTagCompound: NBTTagCompound? = null,
    customName: IChatBaseComponent? = null,
    playerReference: Player? = null,
    nmsSpawnType: EnumMobSpawn = EnumMobSpawn.a,
    ensureSpaceOrSomething: Boolean = true,
    spawnReason: SpawnReason = SpawnReason.DEFAULT
): BukkitEntity? {
    val nmsEntity = type.spawnCreature( // NMS method to spawn an entity from an EntityTypes
        world.toNMS(),  // reference to the NMS world
        nbtTagCompound,  // EntityTag NBT compound
        customName,  // custom name of entity
        playerReference?.toNMS(),  // player reference. used to know if player is OP to apply EntityTag NBT compound
        BlockPosition(this.blockX, this.blockY, this.blockZ),  // the BlockPosition to spawn at
        nmsSpawnType,
        ensureSpaceOrSomething, // does some sort of bounding box checks
        false,
        spawnReason
    ) // not sure. alters the Y position. this is only ever true when using spawn egg and clicked face is UP
    return nmsEntity?.toBukkit()
}

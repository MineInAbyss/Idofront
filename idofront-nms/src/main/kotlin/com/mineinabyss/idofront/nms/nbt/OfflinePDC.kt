package com.mineinabyss.idofront.nms.nbt

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtIo
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.craftbukkit.v1_20_R3.CraftServer
import java.io.File
import java.nio.file.Files
import java.util.*

/**
 * Gets the PlayerData from file for this UUID.
 */
fun UUID.getOfflinePlayerData(): CompoundTag? = (Bukkit.getServer() as CraftServer).server.playerDataStorage.getPlayerData(this.toString())

/**
 * Gets a copy of the WrappedPDC for this OfflinePlayer.
 * Care should be taken to ensure that the player is not online when this is called.
 */
fun OfflinePlayer.getOfflinePDC() : WrappedPDC? {
    if (isOnline) return null
    val baseTag = uniqueId.getOfflinePlayerData()?.getCompound("BukkitValues") ?: return null
    return WrappedPDC(baseTag)
}

/**
 * Saves the given WrappedPDC to the OfflinePlayer's PlayerData file.
 * Care should be taken to ensure that the player is not online when this is called.
 * @return true if successful, false otherwise.
 */
fun OfflinePlayer.saveOfflinePDC(pdc: WrappedPDC): Boolean {
    if (isOnline) return false
    val worldNBTStorage = (Bukkit.getServer() as CraftServer).server.playerDataStorage
    val tempFile = File(worldNBTStorage.playerDir, "$uniqueId.dat.tmp")
    val playerFile = File(worldNBTStorage.playerDir, "$uniqueId.dat")

    val mainPDc = uniqueId.getOfflinePlayerData() ?: return false
    mainPDc.put("BukkitValues", pdc.compoundTag) ?: return false
    runCatching {
        Files.newOutputStream(tempFile.toPath()).use { outStream ->
            NbtIo.writeCompressed(mainPDc, outStream)
            if (playerFile.exists() && !playerFile.delete()) Bukkit.getLogger().severe("Failed to delete player file $uniqueId")
            if (!tempFile.renameTo(playerFile)) Bukkit.getLogger().severe("Failed to rename player file $uniqueId")
        }
    }.onFailure {
        Bukkit.getLogger().severe("Failed to save player file $uniqueId")
        it.printStackTrace()
        return false
    }
    return true
}

inline fun OfflinePlayer.editOfflinePDC(apply: WrappedPDC.() -> Unit): Boolean {
    val pdc = getOfflinePDC() ?: return false
    apply(pdc)
    return saveOfflinePDC(pdc)
}

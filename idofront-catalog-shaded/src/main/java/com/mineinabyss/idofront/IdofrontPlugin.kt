package com.mineinabyss.idofront

import com.mineinabyss.idofront.resourcepacks.MinecraftAssetExtractor
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class IdofrontPlugin : JavaPlugin() {

    override fun onEnable() {
        Bukkit.getAsyncScheduler().runNow(this) {
            MinecraftAssetExtractor.extractLatest()
        }
    }
}

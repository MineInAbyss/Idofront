package com.mineinabyss.idofront.messaging

import org.bukkit.Bukkit

val idofrontLogger = runCatching {
    ComponentLogger.forPlugin(
        Bukkit.getPluginManager().getPlugin("Idofront")!!,
    )
}.getOrElse {
    ComponentLogger.fallback(tag = "Idofront")
}

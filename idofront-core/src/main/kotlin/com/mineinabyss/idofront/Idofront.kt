package com.mineinabyss.idofront

import org.bukkit.plugin.Plugin

object Idofront {
    val plugin: Plugin get() = instance ?: error("Idofront not loaded")

    // TODO set this in idofront plugin startup
    internal var instance: Plugin? = null
}
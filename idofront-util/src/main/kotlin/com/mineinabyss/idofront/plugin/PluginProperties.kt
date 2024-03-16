package com.mineinabyss.idofront.plugin

import org.bukkit.plugin.Plugin

val Plugin.dataPath get() = dataFolder.toPath()

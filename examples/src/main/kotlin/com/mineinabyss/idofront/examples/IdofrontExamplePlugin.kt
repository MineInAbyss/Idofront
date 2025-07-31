package com.mineinabyss.idofront.examples

import com.mineinabyss.idofront.examples.commands.ExampleCommands
import org.bukkit.plugin.java.JavaPlugin

class IdofrontExamplePlugin : JavaPlugin() {
    override fun onEnable() {
        ExampleCommands().registerCommands(this)
    }

    override fun onDisable() {

    }
}

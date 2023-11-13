package com.mineinabyss.idofront.features

import com.mineinabyss.idofront.commands.Command
import com.mineinabyss.idofront.commands.entrypoint.CommandDSLEntrypoint
import org.bukkit.plugin.java.JavaPlugin

abstract class FeatureDSL(
    internal val mainCommandProvider: CommandDSLEntrypoint.(init: Command.() -> Unit) -> Unit = {},
) {
    abstract val plugin: JavaPlugin
    abstract val features: List<Feature>
}

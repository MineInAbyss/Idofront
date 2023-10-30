package com.mineinabyss.idofront.features

import com.mineinabyss.idofront.commands.Command
import com.mineinabyss.idofront.commands.entrypoint.CommandDSLEntrypoint
import org.bukkit.plugin.java.JavaPlugin

abstract class FeatureDSL(
    internal val mainCommandProvider: CommandDSLEntrypoint.(init: Command.() -> Unit) -> Unit = {},
) {
    abstract val plugin: JavaPlugin
    abstract val features: List<Feature>

    internal val rootCommandExtras = mutableListOf<CommandDSLEntrypoint.() -> Unit>()
    internal val mainCommandExtras = mutableListOf<Command.() -> Unit>()
    internal val tabCompletions = mutableListOf<TabCompletion.() -> List<String>?>()

    fun mainCommand(run: Command.() -> Unit) {
        mainCommandExtras += run
    }

    fun tabCompletion(completion: TabCompletion.() -> List<String>?) {
        tabCompletions += completion
    }

    fun commands(command: CommandDSLEntrypoint.() -> Unit) {
        rootCommandExtras += command
    }
}

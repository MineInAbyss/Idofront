package com.mineinabyss.idofront.features

import com.mineinabyss.idofront.commands.Command
import com.mineinabyss.idofront.commands.entrypoint.CommandDSLEntrypoint
import org.bukkit.plugin.Plugin

abstract class FeatureDSL(
    val mainCommandProvider: CommandDSLEntrypoint.() -> Command? = { null },
)  {
    abstract val plugin: Plugin
    abstract val features: List<Feature>

    val rootCommandExtras = mutableListOf<CommandDSLEntrypoint.() -> Unit>()
    val mainCommandExtras = mutableListOf<Command.() -> Unit>()
    val tabCompletions = mutableListOf<TabCompletion.() -> List<String>?>()

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

@file:Suppress("UnstableApiUsage")

package com.mineinabyss.idofront.commands.brigadier

import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.plugin.Plugin

/**
 * Idofront brigader DSL entrypoint.
 */
fun Commands.commands(init: RootIdoCommands.() -> Unit) {
    RootIdoCommands(this).apply(init).buildEach()
}

/**
 * Idofront brigader DSL entrypoint.
 *
 * Must be registered in the plugin's onEnable or onLoad as it hooks into Paper's plugin lifecycle.
 */
fun Plugin.commands(init: RootIdoCommands.() -> Unit) {
    lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
        event.registrar().commands(init)
    }
}

package com.mineinabyss.idofront.commands

import com.mineinabyss.idofront.messaging.logSuccess
import org.bukkit.plugin.java.JavaPlugin

@DslMarker
annotation class CommandMarker

interface Element

@CommandMarker
open class Tag : Element {
    protected fun <T : Element> initTag(command: T, init: T.() -> Unit, addTo: MutableList<T>? = null): T {
        command.init()
        addTo?.add(command)
        return command
    }
}

class CommandHolder(val plugin: JavaPlugin, val commandExecutor: IdofrontCommandExecutor) : Tag() {
    internal val commands = mutableListOf<Command>()

    operator fun get(commandName: String): com.mineinabyss.idofront.commands.Command? =
            commands.firstOrNull { it.names.any { name -> name == commandName } }

    fun command(vararg names: String, topPermission: String = plugin.name.toLowerCase(), init: Command.() -> Unit): Command {
        names.forEach {
            (plugin.getCommand(it) ?: error("Command $it not found")).setExecutor(commandExecutor)
        }
        logSuccess("registered $names!")

        return initTag(Command(names.toList(), topPermission), init, commands)
    }
}
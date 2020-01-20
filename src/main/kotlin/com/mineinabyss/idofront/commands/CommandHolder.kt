package com.mineinabyss.idofront.commands

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

//TODO have reference to command executor and plugin
fun commands(plugin: JavaPlugin, init: CommandHolder.() -> Unit): CommandHolder {
    val commands = CommandHolder(plugin)
    commands.init()
    commandRegisterer.addCommands(commands)
    return commands
}

class CommandHolder(val plugin: JavaPlugin) : Tag() {
    internal val commands = mutableListOf<Command>()

    fun command(vararg names: String, topPermission: String = plugin.name.toLowerCase(), init: Command.() -> Unit): Command {
        names.forEach {
            (plugin.getCommand(it) ?: error("Command $it not found")).setExecutor(commandExecutor)
        }

        return initTag(Command(names.toList(), topPermission), init, commands)
    }
}
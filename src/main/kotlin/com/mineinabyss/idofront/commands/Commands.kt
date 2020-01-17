package com.mineinabyss.idofront.commands

import com.mineinabyss.idofront.broadcast
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin


//TODO have reference to command executor and plugin
fun commands(plugin: JavaPlugin, init: Commands.() -> Unit): Commands {
    val commands = Commands(plugin)
    commands.init()
    return commands
}

class Commands(val plugin: JavaPlugin) : Tag() {
    internal val commands = mutableListOf<Command>()

    fun command(name: String, init: Command.() -> Unit): Command {
        (plugin.getCommand(name) ?: error("Command $name not found"))
                .setExecutor(commandExecutor)
        return initTag(Command(name), init, commands)
    }

    init {
        broadcast("hello!")
    }
}

interface Element

@CommandMarker
open class Tag : Element {
    protected fun <T : Element> initTag(command: T, init: T.() -> Unit, addTo: MutableList<T>? = null): T {
        command.init()
        addTo?.add(command)
        return command
    }
}

class Command(val name: String) : Tag() {
    private val executions = mutableListOf<Execution.() -> Unit>()
    private val subcommands = mutableListOf<Command>()

    class Execution(val sender: CommandSender, val args: List<String>)

    fun onExecute(run: Execution.() -> Unit) = executions.add(run)

    fun execute(sender: CommandSender, args: List<String>) =
            executions.forEach { run -> Execution(sender, args).run() }

    fun command(name: String, init: Command.() -> Unit) = initTag(Command(name), init, subcommands)
    fun textCompletion(init: AutoCompletion.() -> Unit) = initTag(AutoCompletion(mutableListOf<String>()), init)
}

class AutoCompletion(val options: List<*>) : Tag() {
    operator fun String.unaryPlus() {

    }
}

@DslMarker
annotation class CommandMarker
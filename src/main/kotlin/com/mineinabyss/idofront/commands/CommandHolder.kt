package com.mineinabyss.idofront.commands

import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

class CommandHolder(
        private val plugin: JavaPlugin,
        private val commandExecutor: IdofrontCommandExecutor
) : Containable() {
    internal val commands = mutableListOf<CommandCreation>()

    operator fun get(commandName: String): CommandCreation? =
            commands.firstOrNull { it.names.any { name -> name == commandName } }

    fun command(vararg names: String, topPermission: String = plugin.name.toLowerCase(), init: Command.() -> Unit) {
        names.forEach {
            //TODO change error message to be more descriptive
            (plugin.getCommand(it) ?: error("Command $it not found")).setExecutor(commandExecutor)
        }
        commands += CommandCreation(names.toList(), topPermission, listOf(), sharedInit, init)
    }

    fun execute(creation: CommandCreation, sender: CommandSender, args: List<String>){
        val command = creation.newInstance(sender, args)
//        initTag(command, creation.init)
//        sharedInit.forEach { command.it() } //apply all our shared conditions
        command.execute()
    }

    /**
     * Group commands which share methods or variables together, so commands outside this scope can't see them
     */
//    fun commandGroup(init: CommandGroup<Command>.() -> Unit) {
//        CommandGroup(this, sender, args).init()
//    }

    override fun addChild(creation: CommandCreation): CommandCreation {
        commands += creation
        return creation
    }
}
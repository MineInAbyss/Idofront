package com.mineinabyss.idofront.commands

abstract class Containable : Tag() {
    protected val sharedInit = mutableListOf<Command.() -> Unit>()

    /**
     * Will run on creation of all sub-commands in this command group. Useful for sharing conditions.
     *
     * If multiple shared blocks are created, all blocks declared above a command will be executed. Anything below
     * will not, so you can add additional conditions or run additional actions on specific commands.
     */
    fun shared(conditions: Command.() -> Unit) = sharedInit.add(conditions)

    abstract fun addChild(creation: CommandCreation): CommandCreation
}
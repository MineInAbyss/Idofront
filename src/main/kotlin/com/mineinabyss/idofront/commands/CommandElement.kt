package com.mineinabyss.idofront.commands

@DslMarker
annotation class CommandMarker

interface CommandElement

@CommandMarker
open class Tag : CommandElement {
    protected fun <T : CommandElement> initTag(command: T, init: T.() -> Unit, addTo: MutableList<T>? = null): T {
        command.init()
        addTo?.add(command)
        return command
    }
}
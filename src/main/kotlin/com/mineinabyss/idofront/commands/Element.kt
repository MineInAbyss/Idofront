package com.mineinabyss.idofront.commands

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
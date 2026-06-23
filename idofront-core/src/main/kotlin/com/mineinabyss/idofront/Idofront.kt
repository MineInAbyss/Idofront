package com.mineinabyss.idofront

import org.bukkit.plugin.Plugin

@RequiresOptIn(message = "This is an internal Idofront API and should not be used outside of the library.")
annotation class InternalIdofrontApi

object Idofront {
    @OptIn(InternalIdofrontApi::class)
    val plugin: Plugin get() = instance ?: error("Idofront not loaded")

    @InternalIdofrontApi
    var instance: Plugin? = null
}

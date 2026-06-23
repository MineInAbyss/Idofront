package com.mineinabyss.idofront.messaging

import com.mineinabyss.idofront.Idofront

val Idofront.logger
    get() = runCatching { ComponentLogger.forPlugin(plugin) }
        .getOrElse { ComponentLogger.fallback(tag = "Idofront") } // fallback for unit tests

@Deprecated("Binary compatibility, will be removed in the future", replaceWith = ReplaceWith("Idofront.logger"))
val idofrontLogger = Idofront.logger
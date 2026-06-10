package com.mineinabyss.idofront.messaging

import com.mineinabyss.idofront.Idofront

val Idofront.logger
    get() = runCatching { ComponentLogger.forPlugin(plugin) }
        .getOrElse { ComponentLogger.fallback(tag = "Idofront") } // fallback for unit tests

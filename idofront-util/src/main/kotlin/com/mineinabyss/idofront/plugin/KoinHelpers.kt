package com.mineinabyss.idofront.plugin

import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.error.KoinAppAlreadyStartedException
import org.koin.core.module.Module

fun startOrAppendKoin(vararg modules: Module) {
    try {
        startKoin { modules(*modules) }
    } catch (e: KoinAppAlreadyStartedException) {
        loadKoinModules(modules.toList())
    }
}

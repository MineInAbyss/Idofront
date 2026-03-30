package com.mineinabyss.idofront.features

import org.kodein.di.DI
import org.kodein.di.instance

class FeatureInstance(
    val di: DI,
) {
    val context by di.instance<FeatureContext>()

    fun close() {
        context.onClose.forEach { it.close() }
    }
}

class FeatureContext {
    val onClose: MutableList<AutoCloseable> = mutableListOf()
}

package com.mineinabyss.idofront.features

interface Feature {
    val dependsOn: Set<String> get() = setOf()

    fun FeatureDSL.enable() {}

    fun FeatureDSL.disable() {}

}

fun Feature.enable(plugin: FeatureDSL) = plugin.enable()
fun Feature.disable(plugin: FeatureDSL) = plugin.disable()


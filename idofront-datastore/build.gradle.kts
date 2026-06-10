plugins {
    id(miaLibs.plugins.mia.kotlin.jvm.get().pluginId)
    id(miaLibs.plugins.mia.papermc.get().pluginId)
    id(miaLibs.plugins.mia.publication.get().pluginId)
    id(miaLibs.plugins.mia.testing.get().pluginId)
    alias(miaLibs.plugins.kotlinx.serialization)
}

repositories {
    google()
}

dependencies {
    compileOnly(miaLibs.kotlinx.serialization.json)
    compileOnly(miaLibs.minecraft.mccoroutine)
    compileOnly(miaLibs.kotlinx.coroutines)
    compileOnly(miaLibs.sqlite.kt)
    compileOnly(projects.idofrontUtil)
    compileOnly(projects.idofrontCommands)
    compileOnly(projects.idofrontLogging)
    compileOnly(projects.idofrontConfig)
    compileOnly(miaLibs.dependencies)
    api(projects.idofrontCore)
}

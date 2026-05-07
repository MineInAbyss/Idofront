plugins {
    id(idofrontLibs.plugins.mia.kotlin.jvm.get().pluginId)
    id(idofrontLibs.plugins.mia.papermc.get().pluginId)
    id(idofrontLibs.plugins.mia.publication.get().pluginId)
    id(idofrontLibs.plugins.mia.testing.get().pluginId)
    alias(idofrontLibs.plugins.kotlinx.serialization)
}

dependencies {
    compileOnly(idofrontLibs.kotlinx.serialization.json)
    compileOnly(idofrontLibs.minecraft.mccoroutine)
    compileOnly(idofrontLibs.kotlinx.coroutines)
    implementation(projects.idofrontUtil)
    implementation(projects.idofrontCommands)
    implementation(projects.idofrontLogging)
    implementation(projects.idofrontConfig)
    api(idofrontLibs.dependencies)
}

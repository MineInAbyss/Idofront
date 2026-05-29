plugins {
    id(miaLibs.plugins.mia.kotlin.jvm.get().pluginId)
    id(miaLibs.plugins.mia.papermc.get().pluginId)
    id(miaLibs.plugins.mia.publication.get().pluginId)
    id(miaLibs.plugins.mia.testing.get().pluginId)
    alias(miaLibs.plugins.kotlinx.serialization)
}

dependencies {
    compileOnly(miaLibs.kotlinx.serialization.json)
    compileOnly(miaLibs.minecraft.mccoroutine)
    compileOnly(miaLibs.kotlinx.coroutines)
    implementation(projects.idofrontUtil)
    implementation(projects.idofrontCommands)
    implementation(projects.idofrontLogging)
    implementation(projects.idofrontConfig)
    api(miaLibs.dependencies)
}

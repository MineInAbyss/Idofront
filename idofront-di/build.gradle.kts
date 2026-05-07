plugins {
    id(miaConventions.plugins.mia.kotlin.jvm.get().pluginId)
    id(miaConventions.plugins.mia.papermc.get().pluginId)
    id(miaConventions.plugins.mia.publication.get().pluginId)
    id(miaConventions.plugins.mia.testing.get().pluginId)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.minecraft.mccoroutine)
    compileOnly(idofrontLibs.kotlinx.coroutines)
    implementation(projects.idofrontUtil)
    implementation(projects.idofrontCommands)
    implementation(projects.idofrontLogging)
    implementation(projects.idofrontConfig)
    api(idofrontLibs.dependencies)
}

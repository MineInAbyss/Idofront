plugins {
    id(idofrontLibs.plugins.mia.kotlin.jvm.get().pluginId)
    id(idofrontLibs.plugins.mia.papermc.get().pluginId)
    id(idofrontLibs.plugins.mia.publication.get().pluginId)
    id(idofrontLibs.plugins.mia.testing.get().pluginId)
    alias(idofrontLibs.plugins.kotlinx.serialization)
}

dependencies {
    compileOnly(idofrontLibs.kotlinx.serialization.json)
    compileOnly(idofrontLibs.kotlinx.serialization.kaml)
    implementation(projects.idofrontLogging)

    testImplementation(idofrontLibs.kotlinx.serialization.json)
    testImplementation(idofrontLibs.kotlinx.serialization.kaml)
}

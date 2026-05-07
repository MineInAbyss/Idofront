
plugins {
    id(idofrontLibs.plugins.mia.kotlin.jvm.get().pluginId)
    id(idofrontLibs.plugins.mia.papermc.get().pluginId)
    id(idofrontLibs.plugins.mia.publication.get().pluginId)
    alias(idofrontLibs.plugins.kotlinx.serialization)
}

dependencies {
    compileOnly(idofrontLibs.kotlinx.serialization.json)
    compileOnly(idofrontLibs.creative.api)
    compileOnly(idofrontLibs.creative.serializer.minecraft)
    implementation(projects.idofrontLogging)
}

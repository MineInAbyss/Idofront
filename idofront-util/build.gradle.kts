
plugins {
    id(miaLibs.plugins.mia.kotlin.jvm.get().pluginId)
    id(miaLibs.plugins.mia.papermc.get().pluginId)
    id(miaLibs.plugins.mia.publication.get().pluginId)
    alias(miaLibs.plugins.kotlinx.serialization)
}

dependencies {
    compileOnly(miaLibs.kotlinx.serialization.json)
    compileOnly(miaLibs.creative.api)
    compileOnly(miaLibs.creative.serializer.minecraft)
    implementation(projects.idofrontLogging)
}

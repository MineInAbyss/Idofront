plugins {
    id(miaLibs.plugins.mia.kotlin.jvm.get().pluginId)
    id(miaLibs.plugins.mia.papermc.get().pluginId)
    id(miaLibs.plugins.mia.publication.get().pluginId)
    id(miaLibs.plugins.mia.testing.get().pluginId)
    alias(miaLibs.plugins.kotlinx.serialization)
}

dependencies {
    compileOnly(miaLibs.kotlinx.serialization.json)
    compileOnly(miaLibs.kotlinx.serialization.kaml)
    implementation(projects.idofrontLogging)

    testImplementation(miaLibs.kotlinx.serialization.json)
    testImplementation(miaLibs.kotlinx.serialization.kaml)
}

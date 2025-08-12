
plugins {
    id(miaConventions.plugins.mia.kotlin.jvm.get().pluginId)
    id(miaConventions.plugins.mia.papermc.get().pluginId)
    id(miaConventions.plugins.mia.publication.get().pluginId)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.creative.api)
    compileOnly(libs.creative.serializer.minecraft)
    implementation(projects.idofrontLogging)
    implementation(projects.idofrontDi)
}

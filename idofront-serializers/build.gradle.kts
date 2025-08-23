plugins {
    id(miaConventions.plugins.mia.kotlin.jvm.get().pluginId)
    id(miaConventions.plugins.mia.papermc.get().pluginId)
    id(miaConventions.plugins.mia.publication.get().pluginId)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    compileOnly(libs.kotlin.reflect)
    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.kotlinx.serialization.kaml)
    compileOnly(libs.creative.api) { isTransitive = true }
    implementation(projects.idofrontUtil)
    implementation(projects.idofrontLogging)
    implementation(projects.idofrontTextComponents)
    implementation(projects.idofrontDi)
    implementation(projects.idofrontServices)
}

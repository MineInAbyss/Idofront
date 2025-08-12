plugins {
    id(miaConventions.plugins.mia.kotlin.jvm.get().pluginId)
    id(miaConventions.plugins.mia.papermc.get().pluginId)
    id(miaConventions.plugins.mia.publication.get().pluginId)
    id(miaConventions.plugins.mia.testing.get().pluginId)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    compileOnly(libs.kotlinx.serialization.json)
    implementation(projects.idofrontUtil)
    implementation(projects.idofrontDi)
    implementation(projects.idofrontCommands)
    implementation(projects.idofrontLogging)
    implementation(projects.idofrontConfig)
}

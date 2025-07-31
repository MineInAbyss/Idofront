plugins {
    alias(miaConventions.plugins.mia.kotlin.jvm)
    alias(miaConventions.plugins.mia.papermc)
    alias(miaConventions.plugins.mia.publication)
    alias(miaConventions.plugins.mia.testing)
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

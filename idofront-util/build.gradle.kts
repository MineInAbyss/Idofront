
plugins {
    alias(miaConventions.plugins.mia.kotlin.jvm)
    alias(miaConventions.plugins.mia.papermc)
    alias(miaConventions.plugins.mia.publication)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.creative.api)
    compileOnly(libs.creative.serializer.minecraft)
    implementation(projects.idofrontLogging)
    implementation(projects.idofrontDi)
}

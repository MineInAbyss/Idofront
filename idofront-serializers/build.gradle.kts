plugins {
    alias(miaConventions.plugins.mia.kotlin.jvm)
    alias(miaConventions.plugins.mia.papermc)
    alias(miaConventions.plugins.mia.publication)
    alias(miaConventions.plugins.mia.testing)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    compileOnly(libs.kotlin.reflect)
    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.kotlinx.serialization.kaml)
    compileOnly(libs.minecraft.plugin.mythic.dist)
    compileOnly(libs.minecraft.plugin.mythic.crucible)
    compileOnly(libs.minecraft.plugin.nexo)
    compileOnly(libs.minecraft.plugin.itemsadder)
    compileOnly(libs.creative.api) { isTransitive = true }
    implementation(projects.idofrontUtil)
    implementation(projects.idofrontLogging)
    implementation(projects.idofrontTextComponents)
    implementation(projects.idofrontDi)

    testImplementation(libs.kotlinx.serialization.json)
    testImplementation(libs.kotlinx.serialization.kaml)
    testImplementation(libs.minecraft.mockbukkit)
}

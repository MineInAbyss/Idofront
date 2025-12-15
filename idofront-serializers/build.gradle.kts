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
    compileOnly(libs.minecraft.plugin.mythic.dist)
    compileOnly(libs.minecraft.plugin.mythic.crucible)
    compileOnly(libs.minecraft.plugin.nexo)
    compileOnly(libs.minecraft.plugin.itemsadder)
    compileOnly(libs.minecraft.plugin.craftengine.core)
    compileOnly(libs.minecraft.plugin.craftengine.bukkit)
    compileOnly(libs.creative.api) { isTransitive = true }
    implementation(projects.idofrontUtil)
    implementation(projects.idofrontLogging)
    implementation(projects.idofrontTextComponents)
    implementation(projects.idofrontDi)
}

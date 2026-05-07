import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id(idofrontLibs.plugins.mia.kotlin.jvm.get().pluginId)
    id(idofrontLibs.plugins.mia.papermc.get().pluginId)
    id(idofrontLibs.plugins.mia.publication.get().pluginId)
    alias(idofrontLibs.plugins.kotlinx.serialization)
}

dependencies {
    compileOnly(idofrontLibs.kotlin.reflect)
    compileOnly(idofrontLibs.kotlinx.serialization.json)
    compileOnly(idofrontLibs.kotlinx.serialization.kaml)
    compileOnly(idofrontLibs.minecraft.plugin.mythic.dist)
    compileOnly(idofrontLibs.minecraft.plugin.nexo)
    compileOnly(idofrontLibs.creative.api) { isTransitive = true }
    implementation(projects.idofrontUtil)
    implementation(projects.idofrontLogging)
    implementation(projects.idofrontTextComponents)
    implementation(projects.idofrontDi)
    implementation(projects.idofrontServices)
    implementation(idofrontLibs.jsonschema.kt.dsl)
    implementation(idofrontLibs.jsonschema.kt.annotations)
}

val compileKotlin: KotlinCompile by tasks

compileKotlin.compilerOptions {
    freeCompilerArgs.set(listOf("-Xcontext-parameters"))
}
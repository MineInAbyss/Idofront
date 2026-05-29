import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id(miaLibs.plugins.mia.kotlin.jvm.get().pluginId)
    id(miaLibs.plugins.mia.papermc.get().pluginId)
    id(miaLibs.plugins.mia.publication.get().pluginId)
    alias(miaLibs.plugins.kotlinx.serialization)
}

dependencies {
    compileOnly(miaLibs.kotlin.reflect)
    compileOnly(miaLibs.kotlinx.serialization.json)
    compileOnly(miaLibs.kotlinx.serialization.kaml)
    compileOnly(miaLibs.minecraft.plugin.mythic.dist)
    compileOnly(miaLibs.minecraft.plugin.nexo)
    compileOnly(miaLibs.creative.api) { isTransitive = true }
    implementation(projects.idofrontUtil)
    implementation(projects.idofrontLogging)
    implementation(projects.idofrontTextComponents)
    implementation(projects.idofrontDi)
    implementation(projects.idofrontServices)
    implementation(miaLibs.jsonschema.kt.dsl)
    implementation(miaLibs.jsonschema.kt.annotations)
}

val compileKotlin: KotlinCompile by tasks

compileKotlin.compilerOptions {
    freeCompilerArgs.set(listOf("-Xcontext-parameters"))
}
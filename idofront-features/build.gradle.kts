import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id(miaConventions.plugins.mia.kotlin.jvm.get().pluginId)
    id(miaConventions.plugins.mia.papermc.get().pluginId)
    id(miaConventions.plugins.mia.publication.get().pluginId)
    id(miaConventions.plugins.mia.testing.get().pluginId)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    api(libs.koin.core)
    compileOnly(libs.kotlinx.serialization.json)
    implementation(projects.idofrontUtil)
    implementation(projects.idofrontCommands)
    implementation(projects.idofrontLogging)
    implementation(projects.idofrontConfig)
}
val compileKotlin: KotlinCompile by tasks

compileKotlin.compilerOptions {
    freeCompilerArgs.set(listOf("-Xcontext-parameters"))
}
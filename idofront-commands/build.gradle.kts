import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id(idofrontLibs.plugins.mia.kotlin.jvm.get().pluginId)
    id(idofrontLibs.plugins.mia.papermc.get().pluginId)
    id(idofrontLibs.plugins.mia.publication.get().pluginId)
}

dependencies {
    implementation(projects.idofrontLogging)
    implementation(projects.idofrontTextComponents)
    implementation(idofrontLibs.minecraft.mccoroutine)
    implementation(idofrontLibs.kotlinx.coroutines)
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.compilerOptions {
    freeCompilerArgs.set(listOf("-Xcontext-parameters"))
}
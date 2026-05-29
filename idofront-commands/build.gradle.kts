import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id(miaLibs.plugins.mia.kotlin.jvm.get().pluginId)
    id(miaLibs.plugins.mia.papermc.get().pluginId)
    id(miaLibs.plugins.mia.publication.get().pluginId)
}

dependencies {
    implementation(projects.idofrontLogging)
    implementation(projects.idofrontTextComponents)
    implementation(miaLibs.minecraft.mccoroutine)
    implementation(miaLibs.kotlinx.coroutines)
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.compilerOptions {
    freeCompilerArgs.set(listOf("-Xcontext-parameters"))
}
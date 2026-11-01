plugins {
    id(miaLibs.plugins.mia.kotlin.jvm.get().pluginId)
    id(miaLibs.plugins.mia.papermc.get().pluginId)
    id(miaLibs.plugins.mia.testing.get().pluginId)
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
    implementation(projects.idofrontCore)
    implementation(projects.idofrontLogging)
    implementation(projects.idofrontTextComponents)
    implementation(projects.idofrontDi)
    implementation(projects.idofrontServices)
    api(miaLibs.jsonschema.kt.dsl)
    implementation(miaLibs.jsonschema.kt.annotations)

    testImplementation("com.mineinabyss.jsonschema-kt:writer-jvm:0.2.0")
    testImplementation(miaLibs.minecraft.papermc)
    testImplementation(miaLibs.creative.api)
    testImplementation(miaLibs.kotlinx.serialization.json)
    testImplementation(miaLibs.kotlinx.serialization.kaml)
}

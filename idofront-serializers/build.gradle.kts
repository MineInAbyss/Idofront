plugins {
    id("com.mineinabyss.conventions.kotlin.jvm")
    id("com.mineinabyss.conventions.cartridge")
    id("com.mineinabyss.conventions.publication")
    id("com.mineinabyss.conventions.testing")
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
    compileOnly(libs.creative.api)
    implementation(project(":idofront-util"))
    implementation(project(":idofront-logging"))
    implementation(project(":idofront-text-components"))
    implementation(project(":idofront-di"))
    implementation(project(":idofront-nms"))

    testImplementation(libs.kotlinx.serialization.json)
    testImplementation(libs.kotlinx.serialization.kaml)
    testImplementation(libs.minecraft.mockbukkit)
}

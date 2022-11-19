@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.publication")
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    compileOnly(libs.kotlin.reflect)
    compileOnly(libs.kotlinx.serialization.json)
    implementation(project(":idofront-util"))
    implementation(project(":idofront-logging"))
    implementation(project(":idofront-text-components"))
}

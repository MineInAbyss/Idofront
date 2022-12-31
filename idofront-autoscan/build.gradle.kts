@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.publication")
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    api(libs.reflections)
    compileOnly(libs.kotlinx.serialization.json)
    implementation(project(":idofront-logging"))
}

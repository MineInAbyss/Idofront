plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.publication")
    kotlin("plugin.serialization")
}

dependencies {
    api(libs.reflections)
    compileOnly(libs.kotlinx.serialization.json)
    api(project(":"))
}

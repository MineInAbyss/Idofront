plugins {
    id("com.mineinabyss.conventions.kotlin.jvm")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.publication")
}

dependencies {
    implementation(project(":idofront-text-components"))
    implementation(project(":idofront-di"))
    api(libs.kermit)
}

plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://repo.mineinabyss.com/releases")
}

val kotlinVersion: String by project

dependencies {
    implementation(kotlin("gradle-plugin", "1.5.0"))
    implementation(kotlin("serialization", "1.5.0"))
//    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.4.32")
//    implementation("org.jetbrains.kotlin:kotlin-serialization:1.4.32")
    implementation("com.mineinabyss:shared-gradle:0.0.6")
}

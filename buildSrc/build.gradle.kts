import org.gradle.api.Project.GRADLE_PROPERTIES
import java.util.*

plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://repo.mineinabyss.com/releases")
}

// Load properties from root gradle.properties
Properties().apply { load(rootDir.toPath().resolveSibling(GRADLE_PROPERTIES).toFile().inputStream()) }
    .forEach { (key, value) -> project.ext["$key"] = value }

val kotlinVersion: String by project

dependencies {
    implementation(kotlin("gradle-plugin", kotlinVersion))
    implementation(kotlin("serialization", kotlinVersion))
    implementation("com.mineinabyss:shared-gradle:0.0.6")
}

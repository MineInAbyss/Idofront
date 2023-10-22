import org.jetbrains.kotlin.util.*
import java.util.*

// Load properties from root gradle.properties
Properties()
    .apply { load(rootDir.toPath().resolveSibling(Project.GRADLE_PROPERTIES).toFile().inputStream()) }
    .forEach { (key, value) -> project.ext["$key"] = value }

plugins {
    `kotlin-dsl`
    `maven-publish`
}

//TODO duplicated code, try to get version from other project somehow
val releaseVersion: String? = System.getenv("RELEASE_VERSION")
val isSnapshot = System.getenv("IS_SNAPSHOT") == "true"

fun getNextVersion(): String {
    if (isSnapshot) return "$version".suffixIfNot("-SNAPSHOT")
    if (releaseVersion == null) return "$version"

    val (majorTarget, minorTarget) = version.toString().split(".")
    try {
        val (major, minor, patch) = releaseVersion.removePrefix("v").removeSuffix("-SNAPSHOT").split(".")
        if (majorTarget == major && minorTarget == minor) {
            return "$major.$minor.${patch.toInt() + 1}"
        }
    } catch (_: Exception) {
    }
    return "$majorTarget.$minorTarget.0"
}

version = getNextVersion()

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://repo.mineinabyss.com/releases")
}

dependencies {
    implementation(libs.gradle.kotlin)
    implementation(libs.gradle.dokka)
    implementation(libs.gradle.shadowjar)
    implementation(libs.gradle.paperweight.userdev)
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

publishing {
    repositories {
        maven("https://repo.mineinabyss.com/releases") {
            credentials {
                username = project.findProperty("mineinabyssMavenUsername") as String?
                password = project.findProperty("mineinabyssMavenPassword") as String?
            }
        }
    }
}

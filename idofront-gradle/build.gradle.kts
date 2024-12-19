import java.util.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach
import kotlin.collections.lastOrNull

// Load properties from root gradle.properties
Properties()
    .apply { load(rootDir.toPath().resolveSibling(Project.GRADLE_PROPERTIES).toFile().inputStream()) }
    .forEach { (key, value) -> project.ext["$key"] = value }

plugins {
    `kotlin-dsl`
    `maven-publish`
}

//TODO duplicated code, try to get version from other project somehow
version = project.ext["version"] as String

fun getNextVersion(): String {
    val releaseVersion: String? = System.getenv("RELEASE_VERSION")
    val branchVersion: String? = System.getenv("BRANCH_VERSION")
    val isSnapshot = System.getenv("IS_SNAPSHOT") == "true"

    if (releaseVersion == null) return "$version-dev"

    val (major, minor) = version.toString().split(".").takeIf { it.size >= 2 }
        ?: error("Version $version does not match major.minor format!")
    fun bump(bump: String?, matchPatch: String? = null) = bump
        ?.removePrefix("v")
        ?.replace(Regex("-\\w*"), "")
        ?.split(".")
        ?.takeIf { it.size > 2 && it[0] == major && it[1] == minor && (matchPatch == null || it.getOrNull(2) == matchPatch) }
        ?.lastOrNull()?.toInt()?.plus(1)
        ?: 0

    return buildString {
        val bumpedPatch = bump(releaseVersion)
        append("$major.$minor.$bumpedPatch")
        if (isSnapshot) append("-dev.${bump(branchVersion, bumpedPatch.toString())}")
    }
}

version = "${getNextVersion()}+mc.${libs.versions.minecraft.get().substringBefore("-R")}"

kotlin {
    jvmToolchain(17)
}

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
        maven {
            val repo = "https://repo.mineinabyss.com/"
            val isSnapshot = System.getenv("IS_SNAPSHOT") == "true"
            val url = if (isSnapshot) repo + "snapshots" else repo + "releases"
            setUrl(url)
            credentials {
                username = project.findProperty("mineinabyssMavenUsername") as String?
                password = project.findProperty("mineinabyssMavenPassword") as String?
            }
        }
    }
}

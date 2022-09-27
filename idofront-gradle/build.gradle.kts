import java.util.*

// Load properties from root gradle.properties
Properties()
    .apply { load(rootDir.toPath().resolveSibling(Project.GRADLE_PROPERTIES).toFile().inputStream()) }
    .forEach { (key, value) -> project.ext["$key"] = value }

plugins {
    `kotlin-dsl`
    `maven-publish`
}

val kotlinVersion: String by project
val releaseVersion: String? = System.getenv("RELEASE_VERSION")

//TODO duplicated code, try to get version from other project somehow
version = (if (releaseVersion != null) {
    val (major, minor, patch) = releaseVersion.split(".")
    val (majorTarget, minorTarget) = version.toString().split(".")
    if (majorTarget == major && minorTarget == minor) {
        "$major.$minor.${patch.toInt() + 1}"
    } else {
        "$majorTarget.$minorTarget.0"
    }
} else "$version-dev")

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://repo.mineinabyss.com/releases")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(kotlin("gradle-plugin", kotlinVersion))
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.6.10")
    implementation("gradle.plugin.com.github.jengelman.gradle.plugins:shadow:7.0.0")
    implementation("io.papermc.paperweight.userdev:io.papermc.paperweight.userdev.gradle.plugin:1.3.9-SNAPSHOT")
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
//    implementation(kotlin("stdlib-jdk8", kotlinVersion))
//    implementation(kotlin("reflect", kotlinVersion))
}

java {
    withSourcesJar()
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(17))
    }
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

tasks {
    processResources {
        filesMatching("mineinabyss-conventions.properties") {
            expand(
                mutableMapOf(
                    "miaConventionsVersion" to version,
                    "miaConventionsKotlinVersion" to kotlinVersion,
                )
            )
        }
    }

    publish {
        dependsOn("check")
    }

    build {
        dependsOn(processResources)
    }
}

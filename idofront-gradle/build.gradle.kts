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
val runNumber = System.getenv("GITHUB_RUN_NUMBER") ?: "DEV"
val idofrontVersion = "${project.ext["version"]}.$runNumber"
version = "$kotlinVersion-$runNumber"

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://repo.mineinabyss.com/releases")
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    implementation(kotlin("gradle-plugin", kotlinVersion))
    implementation(kotlin("serialization", kotlinVersion))
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.6.10")
    implementation("gradle.plugin.com.github.jengelman.gradle.plugins:shadow:7.0.0")
    implementation("io.papermc.paperweight.userdev:io.papermc.paperweight.userdev.gradle.plugin:1.3.5")
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
                    "miaIdofrontVersion" to idofrontVersion
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

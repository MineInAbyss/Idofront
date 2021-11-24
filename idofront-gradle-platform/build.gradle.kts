import Com_mineinabyss_conventions_platform_gradle.Deps

// Load properties from root gradle.properties
java.util.Properties()
    .apply { load(rootDir.toPath().resolveSibling(Project.GRADLE_PROPERTIES).toFile().inputStream()) }
    .forEach { (key, value) -> project.ext["$key"] = value }

plugins {
    `java-platform`
    `maven-publish`
    id("com.github.ben-manes.versions") version "0.39.0"
    id("com.mineinabyss.conventions.platform")
}

val kotlinVersion: String by project
val runNumber = System.getenv("GITHUB_RUN_NUMBER") ?: "DEV"

version = "$kotlinVersion-$runNumber"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://minecraft.curseforge.com/api/maven/")
    maven("https://erethon.de/repo/")
}

dependencies {
    constraints {
        api("${Deps.kotlin.reflect}:$kotlinVersion")

        api("${Deps.kotlinx.serialization.json}:1.3.1")
        api("${Deps.kotlinx.serialization.cbor}:1.3.1")
        api("${Deps.kotlinx.serialization.hocon}:1.3.1")
        api("${Deps.kotlinx.serialization.protobuf}:1.3.1")
        api("${Deps.kotlinx.serialization.properties}:1.3.1")
        api("${Deps.kotlinx.serialization.kaml}:0.37.0")

        api("${Deps.kotlinx.coroutines}:1.5.2")
        api("${Deps.exposed.core}:0.34.2")
        api("${Deps.exposed.dao}:0.34.2")
        api("${Deps.exposed.jdbc}:0.34.2")

        api("${Deps.minecraft.headlib}:3.0.6")
        api("${Deps.minecraft.skedule}:1.2.6")
        api("${Deps.`kotlin-statistics`}:1.2.1")
        api("${Deps.`sqlite-jdbc`}:3.36.0.2")
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
    publications {
        create<MavenPublication>("maven") {
            from(components["javaPlatform"])
        }
    }
}

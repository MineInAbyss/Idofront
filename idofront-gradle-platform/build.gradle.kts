import Com_mineinabyss_conventions_platform_gradle.*

// Load properties from root gradle.properties
java.util.Properties()
    .apply { load(rootDir.toPath().resolveSibling(Project.GRADLE_PROPERTIES).toFile().inputStream()) }
    .forEach { (key, value) -> project.ext["$key"] = value }

val kotlinVersion: String by project

plugins {
    `java-platform`
    `maven-publish`
    id("com.github.ben-manes.versions") version "0.39.0"
    id("com.mineinabyss.conventions.platform")
}

version = kotlinVersion

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://minecraft.curseforge.com/api/maven/")
    maven("https://erethon.de/repo/")
}

dependencies {
    constraints {
        api("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")

        api("${Deps.kotlinx.serialization.json}:1.2.2")
        api("${Deps.kotlinx.serialization.cbor}:1.2.2")
        api("${Deps.kotlinx.serialization.kaml}:0.35.2")

        api("${Deps.kotlinx.coroutines}:1.5.1")
        api("${Deps.exposed.core}:0.33.1")
        api("${Deps.exposed.dao}:0.33.1")
        api("${Deps.exposed.jdbc}:0.33.1")

        api("${Deps.minecraft.headlib}:3.0.6")
        api("${Deps.minecraft.skedule}:1.2.6")
        api("${Deps.`kotlin-statistics`}:1.2.1")
        api("${Deps.`sqlite-jdbc`}:3.36.0.1")
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

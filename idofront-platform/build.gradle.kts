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
    maven("https://repo.codemc.io/repository/maven-snapshots/")
}

dependencies {
    constraints {
        api("${Deps.kotlin.reflect}:$kotlinVersion")

        val serializationVersion = "1.3.1"
        api("${Deps.kotlinx.serialization.json}:$serializationVersion")
        api("${Deps.kotlinx.serialization.cbor}:$serializationVersion")
        api("${Deps.kotlinx.serialization.hocon}:$serializationVersion")
        api("${Deps.kotlinx.serialization.protobuf}:$serializationVersion")
        api("${Deps.kotlinx.serialization.properties}:$serializationVersion")
        api("${Deps.kotlinx.serialization.kaml}:0.38.0")

        api("${Deps.kotlinx.coroutines}:1.5.2")

        val exposedVersion = "0.36.1"
        api("${Deps.exposed.core}:$exposedVersion")
        api("${Deps.exposed.dao}:$exposedVersion")
        api("${Deps.exposed.jdbc}:$exposedVersion")
        api("${Deps.exposed.`java-time`}:$exposedVersion")

        api("${Deps.minecraft.headlib}:6fb3b62fd3")
        api("${Deps.minecraft.skedule}:1.2.6")
        api("${Deps.minecraft.anvilgui}:1.5.3-SNAPSHOT")

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

tasks {
    build {
        dependsOn(project(":shaded").tasks.build)
    }
}

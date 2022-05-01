// Load properties from root gradle.properties
java.util.Properties()
    .apply { load(rootDir.toPath().resolveSibling(Project.GRADLE_PROPERTIES).toFile().inputStream()) }
    .forEach { (key, value) -> project.ext["$key"] = value }

plugins {
    `java-platform`
    `maven-publish`
    id("com.github.ben-manes.versions") version "0.39.0"
}

val kotlinVersion: String by project
val runNumber = System.getenv("GITHUB_RUN_NUMBER")
version = "${project.ext["version"]}${if(runNumber != null) ".$runNumber" else ""}"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
}

dependencies {
    // REMEMBER TO ADD TO SHADED AS WELL!
    constraints {
        api(libs.kotlin.reflect)

        api(libs.kotlinx.serialization.json)
        api(libs.kotlinx.serialization.cbor)
        api(libs.kotlinx.serialization.hocon)
        api(libs.kotlinx.serialization.protobuf)
        api(libs.kotlinx.serialization.properties)
        api(libs.kotlinx.serialization.kaml)

        api(libs.kotlinx.coroutines)

        api(libs.exposed.core)
        api(libs.exposed.dao)
        api(libs.exposed.jdbc)
        api(libs.exposed.javatime)

        api(libs.minecraft.headlib)
        api(libs.minecraft.mccoroutine)
        api(libs.minecraft.anvilgui)

        api(libs.kotlin.statistics)
        api(libs.sqlite.jdbc)

        api(libs.koin.core)

        // Not shaded

        api(libs.koin.ktor)
        api(libs.koin.test)
        api(libs.koin.test.junit5)
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

import Com_mineinabyss_conventions_platform_gradle.Deps

plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.copyjar")
    id("com.github.johnrengelman.shadow")
}

val runNumber: String = System.getenv("GITHUB_RUN_NUMBER") ?: "DEV"
val kotlinVersion: String by project

version = "$kotlinVersion-$runNumber"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://erethon.de/repo/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(Deps.kotlin.reflect)

    implementation(Deps.kotlinx.serialization.json)
    implementation(Deps.kotlinx.serialization.cbor)
    implementation(Deps.kotlinx.serialization.hocon)
    implementation(Deps.kotlinx.serialization.protobuf)
    implementation(Deps.kotlinx.serialization.properties)
    implementation(Deps.kotlinx.serialization.kaml)

    implementation(Deps.kotlinx.coroutines)
    implementation(Deps.exposed.core)
    implementation(Deps.exposed.dao)
    implementation(Deps.exposed.jdbc)
    implementation(Deps.exposed.`java-time`)

    // The other JitPack dependency doesn't include the actual version-speicifc code, only API
    implementation("de.erethon:headlib:3.0.9")
    implementation(Deps.minecraft.skedule)
    implementation(Deps.minecraft.anvilgui)

    implementation(Deps.`kotlin-statistics`)
    implementation(Deps.`sqlite-jdbc`)
}

tasks {
    shadowJar {
        archiveBaseName.set("mineinabyss")
        archiveClassifier.set("")
        archiveExtension.set("platform")
    }
}

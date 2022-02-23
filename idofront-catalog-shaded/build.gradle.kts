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
    maven("https://repo.codemc.io/repository/maven-snapshots/")
}

dependencies {
    // The other JitPack dependency doesn't include the actual version-speicifc code, only API
    implementation("de.erethon:headlib:3.0.9")
    implementation(libs.minecraft.skedule)
    implementation(libs.minecraft.anvilgui)

    implementation(libs.reflections)

    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.javatime)
    implementation(libs.sqlite.jdbc)

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)

    implementation(libs.kotlinx.coroutines)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.cbor)
    implementation(libs.kotlinx.serialization.hocon)
    implementation(libs.kotlinx.serialization.protobuf)
    implementation(libs.kotlinx.serialization.properties)
    implementation(libs.kotlinx.serialization.kaml)

    implementation(libs.kotlin.statistics)

    implementation(libs.koin.core)
    implementation(libs.koin.ktor)


    implementation(libs.ktor.serialization)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.kmongo.coroutine.serialization)
}

tasks {
    shadowJar {
        archiveBaseName.set("mineinabyss")
        archiveClassifier.set("")
        archiveExtension.set("platform")
    }
}

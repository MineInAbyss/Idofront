plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.copyjar")
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://repo.mineinabyss.com/releases")
    maven("https://erethon.de/repo/")
}

dependencies {
    // The other JitPack dependency doesn't include the actual version-speicifc code, only API
    implementation(libs.minecraft.headlib.core)
    implementation(libs.minecraft.mccoroutine.core)
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

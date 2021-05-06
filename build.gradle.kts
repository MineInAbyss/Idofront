import com.mineinabyss.sharedSetup
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    idea
    `maven-publish`
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.dokka") version "1.4.32"
    id("com.mineinabyss.shared-gradle")
    id("idofront.minecraft-conventions")
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

val serverVersion: String by project

dependencies {
    compileOnly( "org.spigotmc:spigot-api:serverVersion")
    compileOnly( "org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compileOnly( "org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.0")
    compileOnly( "com.charleskorn.kaml:kaml:0.31.0")

//    testCompile( "junit:junit:4.12")
}

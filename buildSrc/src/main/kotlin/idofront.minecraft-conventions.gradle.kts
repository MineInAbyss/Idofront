import com.mineinabyss.sharedSetup
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `maven-publish`
    java
    idea
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.mineinabyss.shared-gradle")
}

sharedSetup {
    applyJavaDefaults()
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    compileOnly( "org.spigotmc:spigot-api:${IdofrontDeps.serverVersion}")
    compileOnly( "org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compileOnly( "org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
    compileOnly( "com.charleskorn.kaml:kaml:0.25.0")

//    testCompile( "junit:junit:4.12")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf(
                "-Xopt-in=kotlin.RequiresOptIn",
            )
        }
    }
}

publishing {
    mineInAbyss(project) {
        from(components["java"])
    }
}

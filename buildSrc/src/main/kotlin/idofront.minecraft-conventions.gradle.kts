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
    addGithubRunNumber()
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

val serverVersion: String by project

dependencies {
    compileOnly( "org.spigotmc:spigot-api:$serverVersion")
    compileOnly( "org.jetbrains.kotlin:kotlin-stdlib-jdk8")
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

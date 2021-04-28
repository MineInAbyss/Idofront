import com.mineinabyss.sharedSetup
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    idea
    `maven-publish`
    kotlin("jvm") version IdofrontDeps.kotlinVersion
    kotlin("plugin.serialization") version IdofrontDeps.kotlinVersion
    id("org.jetbrains.dokka") version IdofrontDeps.kotlinVersion
    id("com.mineinabyss.shared-gradle") version "0.0.6"
}

allprojects {
    apply(plugin="kotlin")
    apply(plugin="maven-publish")

    sharedSetup {
        addGithubRunNumber()
        applyJavaDefaults()
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        compileOnly(kotlin("stdlib-jdk8"))
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
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    compileOnly( "org.spigotmc:spigot-api:${IdofrontDeps.serverVersion}")
    compileOnly( "org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compileOnly( "org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
    compileOnly( "com.charleskorn.kaml:kaml:0.25.0")

    testCompile( "junit:junit:4.12")
}

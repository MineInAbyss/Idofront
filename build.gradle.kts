import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    java
    alias(libs.plugins.kotlin.jvm) apply false
    id("com.mineinabyss.conventions.autoversion")
    alias(libs.plugins.dependencyversions)
}

subprojects {
    repositories {
        mavenCentral()
        maven("https://repo.mineinabyss.com/releases")
        maven("https://erethon.de/repo/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.codemc.io/repository/maven-snapshots/")
        maven("https://jitpack.io")
        maven("https://mvn.lumine.io/repository/maven/")
        maven("https://repo.unnamed.team/repository/unnamed-public/")
    }

    tasks {
        withType<KotlinCompile> {
            kotlinOptions {
                freeCompilerArgs = listOf(
                    "-opt-in=kotlin.RequiresOptIn",
                    "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
                )
            }
        }
    }
}

repositories {
    mavenCentral()
}

tasks {
    register("publish") {
        dependsOn(gradle.includedBuilds.map { it.task(":publish") })
    }

    register("publishToMavenLocal") {
        dependsOn(gradle.includedBuilds.map { it.task(":publishToMavenLocal") })
    }

    build {
        dependsOn(gradle.includedBuilds.map { it.task(":build") })
    }
}

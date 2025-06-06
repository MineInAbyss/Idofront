import org.jetbrains.kotlin.gradle.internal.ensureParentDirsCreated
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import kotlin.io.path.createFile
import kotlin.io.path.notExists
import kotlin.io.path.writeText

plugins {
    java
    alias(libs.plugins.kotlin.jvm) apply false
    id("com.mineinabyss.conventions.autoversion")
    alias(libs.plugins.dependencyversions)
    alias(libs.plugins.version.catalog.update)
}

subprojects {
    repositories {
        mavenCentral()
        maven("https://repo.mineinabyss.com/releases")
        maven("https://repo.mineinabyss.com/snapshots")
        maven("https://erethon.de/repo/")
        mavenLocal()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.spaceio.xyz/repository/maven-snapshots/")
        maven("https://jitpack.io")
        maven("https://mvn.lumine.io/repository/maven-public/") { metadataSources { artifact() } }// MythicMobs
        maven("https://repo.unnamed.team/repository/unnamed-public/")
        maven("https://repo.nexomc.com/releases")
        maven("https://repo.nexomc.com/snapshots")
    }

    tasks {
        withType<KotlinCompile> {
            compilerOptions {
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

    dependencyUpdates {
        rejectVersionIf {
            isNonStable(candidate.version)
        }
    }
    task("versionInfo") {
        file("build/versions.md").apply { ensureParentDirsCreated() }.toPath()
            .also { if (it.notExists()) it.createFile() }
            .writeText(
                """
                ### Built for
                | Minecraft | `${libs.versions.minecraft.get().substringBefore("-R")}` |
                |-----------|--|
                | Kotlin | `${libs.versions.kotlin.get()}` |
                | Java | `${libs.versions.java.get()}` |
                
            """.trimIndent()
            )
    }
}

fun isNonStable(version: String): Boolean {
    val unstableKeywords = listOf(
        "-beta",
        "-rc",
        "-alpha",
    )

    return unstableKeywords.any { version.contains(it, ignoreCase = true) }
}

versionCatalogUpdate {
    keep {
        keepUnusedPlugins = true
        keepUnusedVersions = true
        keepUnusedLibraries = true
    }
}

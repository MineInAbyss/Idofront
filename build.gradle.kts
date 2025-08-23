import org.jetbrains.kotlin.gradle.internal.ensureParentDirsCreated
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import kotlin.io.path.createFile
import kotlin.io.path.notExists
import kotlin.io.path.writeText

plugins {
    java
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.dependencyversions)
    alias(libs.plugins.version.catalog.update)
    alias(miaConventions.plugins.mia.docs)
}

subprojects {
    repositories {
        mavenCentral()
        maven("https://repo.mineinabyss.com/releases")
        maven("https://repo.mineinabyss.com/snapshots")
        maven("https://repo.mineinabyss.com/mirror")
        maven("https://repo.papermc.io/repository/maven-public/")
        mavenLocal()
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
    maven("https://repo.mineinabyss.com/releases")
    maven("https://repo.mineinabyss.com/snapshots")
}

tasks {
    dependencyUpdates {
        rejectVersionIf {
            isNonStable(candidate.version)
        }
    }

    register("versionInfo") {
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
        keepUnusedVersions = true
    }
}

idofront {
    docsVersion = "0.0.4"
}

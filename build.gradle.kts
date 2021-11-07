import Com_mineinabyss_conventions_platform_gradle.Deps
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.publication")
    id("com.mineinabyss.conventions.testing")
    kotlin("plugin.serialization")
}

dependencies {
    compileOnly(Deps.kotlinx.serialization.json)
    compileOnly(Deps.kotlinx.serialization.kaml)
}

allprojects {
    tasks {
        withType<KotlinCompile> {
            kotlinOptions {
                freeCompilerArgs = listOf(
                    "-Xopt-in=kotlin.RequiresOptIn",
                )
            }
        }
    }
}

fun included(build: String, task: String) = gradle.includedBuild(build).task(task)

tasks.publish {
    dependsOn(gradle.includedBuilds.map { it.task(":publish")})
    dependsOn(subprojects.map { it.tasks.publish })
}

tasks.publishToMavenLocal {
    dependsOn(gradle.includedBuilds.map { it.task(":publishToMavenLocal")})
    dependsOn(subprojects.map { it.tasks.publishToMavenLocal })
}

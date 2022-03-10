import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.publication")
    id("com.mineinabyss.conventions.testing")
    kotlin("plugin.serialization")
}

dependencies {
    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.kotlinx.serialization.kaml)
    compileOnly(libs.koin.core)

    api(project("idofront-platform-loader"))
    api(project("idofront-features"))
}

allprojects {
    tasks {
        withType<KotlinCompile> {
            kotlinOptions {
                freeCompilerArgs = listOf(
                    "-opt-in=kotlin.RequiresOptIn",
                )
            }
        }
    }
}

fun included(build: String, task: String) = gradle.includedBuild(build).task(task)

tasks {
    publish {
        dependsOn(gradle.includedBuilds.map { it.task(":publish") })
//        dependsOn(subprojects.map { it.tasks.publish })
    }

    publishToMavenLocal {
        dependsOn(gradle.includedBuilds.map { it.task(":publishToMavenLocal") })
//        dependsOn(subprojects.map { it.tasks.publishToMavenLocal })
    }

    build {
        dependsOn(gradle.includedBuilds.map { it.task(":build") })
        dependsOn(subprojects.map { it.tasks.build })
    }
}

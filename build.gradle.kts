import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    java
    alias(libs.plugins.kotlin.jvm) apply false
    id("com.mineinabyss.conventions.autoversion")
}

allprojects {
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

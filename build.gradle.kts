import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") apply false
    id("com.mineinabyss.conventions.publication")
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

pluginManagement {
    val kotlinVersion: String by settings
    val sharedGradleVersion: String by settings

    repositories {
        gradlePluginPortal()
        maven("https://repo.mineinabyss.com/releases")
    }

    plugins {
        kotlin("plugin.serialization") version kotlinVersion
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.startsWith("com.mineinabyss.conventions"))
                useVersion(sharedGradleVersion)
        }
    }
}

rootProject.name = "idofront"

include(
    "idofront-nms",
)

project(":idofront-nms").projectDir = file("./nms")

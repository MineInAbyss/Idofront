pluginManagement {
    val kotlinVersion: String by settings

    repositories {
        gradlePluginPortal()
        mavenLocal()
        maven("https://repo.mineinabyss.com/releases")
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
    }
}

rootProject.name = "idofront"

includeBuild("idofront-gradle")
includeBuild("idofront-platform")

include(
    "idofront-autoscan",
    "idofront-nms",
    "idofront-catalog",
    "idofront-catalog-shaded",
    "idofront-features",
    "idofront-platform-loader",
)

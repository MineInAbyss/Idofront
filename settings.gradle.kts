pluginManagement {
    val kotlinVersion: String by settings

    repositories {
        mavenLocal()
        gradlePluginPortal()
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

include(
    "idofront-autoscan",
    "idofront-catalog",
    "idofront-catalog-shaded",
    "idofront-commands",
    "idofront-config",
//    "idofront-features",
    "idofront-fonts",
    "idofront-logging",
    "idofront-platform-loader",
    "idofront-nms",
    "idofront-serializers",
    "idofront-text-components",
    "idofront-util",
)

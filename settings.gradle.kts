pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.mineinabyss.com/releases")
    }
}

rootProject.name = "idofront"

include(
    "idofront-nms",
)

project(":idofront-nms").projectDir = file("./nms")

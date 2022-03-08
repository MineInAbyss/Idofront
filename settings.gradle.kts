pluginManagement {
    val kotlinVersion: String by settings
    val miaConventionsVersion: String by settings

    repositories {
        gradlePluginPortal()
        maven("https://repo.mineinabyss.com/releases")
    }

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
    }

//    resolutionStrategy {
//        eachPlugin {
//            if (requested.id.id.startsWith("com.mineinabyss.conventions"))
//                useVersion(miaConventionsVersion)
//        }
//    }
}

rootProject.name = "idofront"

includeBuild("idofront-gradle")
includeBuild("idofront-platform")

include(
    "idofront-catalog",
    "idofront-catalog-shaded",
    "idofront-features",
    "idofront-platform-loader",
)

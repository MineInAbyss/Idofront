enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven("https://repo.mineinabyss.com/releases")
        maven("https://repo.papermc.io/repository/maven-public/")
    }
//    includeBuild("../gradle-conventions")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    val miaLibs: String by settings
    repositories {
        maven("https://repo.mineinabyss.com/releases")
        maven("https://repo.mineinabyss.com/snapshots")
        mavenLocal()
    }
    versionCatalogs {
        create("miaLibs") {
            from("com.mineinabyss:catalog:$miaLibs")
        }
    }
}

rootProject.name = "idofront"

val projects = listOf(
    "idofront-catalog-shaded",
    "idofront-commands",
    "idofront-config",
    "idofront-di",
    "idofront-fonts",
    "idofront-logging",
    "idofront-nms",
    "idofront-serializers",
    "idofront-text-components",
    "idofront-services",
    "idofront-util",
    "examples",
)

include(projects)

gradle.lifecycle.beforeProject {
    repositories {
        mavenCentral()
        maven("https://repo.mineinabyss.com/releases")
        maven("https://repo.mineinabyss.com/snapshots")
        maven("https://repo.mineinabyss.com/mirror")
        maven("https://repo.papermc.io/repository/maven-public/")
        mavenLocal()
    }
}
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven("https://repo.mineinabyss.com/releases")
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    val catalogVersion: String by settings
    repositories {
        maven("https://repo.mineinabyss.com/releases")
        maven("https://repo.mineinabyss.com/snapshots")
        mavenLocal()
    }
    versionCatalogs {
        create("idofrontLibs") {
            from("com.mineinabyss:catalog:$catalogVersion")
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
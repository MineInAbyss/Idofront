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

val conventionsVersion: String by settings

dependencyResolutionManagement {
    repositories {
        maven("https://repo.mineinabyss.com/releases")
        maven("https://repo.mineinabyss.com/snapshots")
        mavenLocal()
    }
    versionCatalogs {
        create("miaConventions") {
            from("com.mineinabyss.conventions:catalog:$conventionsVersion")
        }
        create("idofrontLibs") {
            from(files("gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "idofront"

val projects = listOf(
    "idofront-catalog",
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
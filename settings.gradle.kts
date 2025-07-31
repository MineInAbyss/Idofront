enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven("https://repo.mineinabyss.com/releases")
        maven("https://repo.papermc.io/repository/maven-public/")
    }
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
    "idofront-features",
    "idofront-fonts",
    "idofront-logging",
    "idofront-nms",
    "idofront-serializers",
    "idofront-text-components",
    "idofront-util",
    "examples",
)

include(projects)

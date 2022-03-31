pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.mineinabyss.com/releases")
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs").from(files("../gradle/libs.versions.toml"))
    }
}

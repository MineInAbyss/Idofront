import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    java
}

val libs = the<LibrariesForLibs>()

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly(libs.minecraft.papermc)
}

tasks {
    processResources {
        filesMatching(setOf("plugin.yml", "paper-plugin.yml")) {
            expand(mutableMapOf("plugin_version" to version))
        }
    }
}

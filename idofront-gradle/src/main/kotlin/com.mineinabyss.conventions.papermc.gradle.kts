val serverVersion: String by project

plugins {
    java
}

repositories {
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19-R0.1-SNAPSHOT")
}

tasks {
    processResources {
        filesMatching("plugin.yml") {
            expand(mutableMapOf("plugin_version" to version))
        }
    }
}

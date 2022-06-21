val serverVersion: String by project

plugins {
    java
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:$serverVersion")
}

tasks {
    processResources {
        filesMatching("plugin.yml") {
            expand(mutableMapOf("plugin_version" to version))
        }
    }
}

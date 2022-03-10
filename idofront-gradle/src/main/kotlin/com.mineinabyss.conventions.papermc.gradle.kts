val serverVersion: String by project

plugins {
    java
}

repositories {
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    // Use old papermc groupId with versions below 1.17
    val (major, minor) = serverVersion.split('.').take(2).map { it.toInt() }
    val paperGroup =
        if (major == 1 && minor < 17) "com.destroystokyo.paper"
        else "io.papermc.paper"

    compileOnly("$paperGroup:paper-api:$serverVersion")
}

tasks {
    processResources {
        filesMatching("plugin.yml") {
            expand(mutableMapOf("plugin_version" to version))
        }
    }
}

plugins {
    java
}

val libs = idofrontLibsRef

repositories {
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    mavenLocal()
}

dependencies {
    compileOnly(libs.findLibrary("minecraft-papermc").get())
}

tasks {
    processResources {
        filesMatching(setOf("plugin.yml", "paper-plugin.yml")) {
            expand(mutableMapOf("plugin_version" to version))
        }
    }
}

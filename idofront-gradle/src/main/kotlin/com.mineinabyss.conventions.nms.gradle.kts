val serverVersion: String by project

plugins {
    java
    id("io.papermc.paperweight.userdev")
}

repositories {
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    paperDevBundle(serverVersion)
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
}

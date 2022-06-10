val serverVersion: String by project

plugins {
    java
    id("io.papermc.paperweight.userdev")
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    mavenLocal()
}

dependencies {
    paperDevBundle(serverVersion)
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
}

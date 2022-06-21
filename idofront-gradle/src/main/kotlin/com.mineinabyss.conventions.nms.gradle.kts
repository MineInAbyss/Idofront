val serverVersion: String by project

plugins {
    java
    id("io.papermc.paperweight.userdev")
}

dependencies {
    paperDevBundle(serverVersion)
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
}

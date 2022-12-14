import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    java
    id("io.papermc.paperweight.userdev")
}

val serverVersion: String? by project
val libs = the<LibrariesForLibs>()

dependencies {
    paperDevBundle(serverVersion ?: libs.versions.minecraft.get())
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
}

import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    java
    io.papermc.paperweight.userdev
}

val libs = the<LibrariesForLibs>()

dependencies {
    paperweight.paperDevBundle(libs.versions.minecraft.get())
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
}

import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    java
    id("io.papermc.paperweight.userdev")
}

val libs = the<LibrariesForLibs>()

dependencies {
    paperDevBundle(libs.versions.minecraft.get())
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
}

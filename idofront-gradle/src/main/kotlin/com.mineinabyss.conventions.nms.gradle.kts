plugins {
    java
    io.papermc.paperweight.userdev
}

val libs = idofrontLibsRef

dependencies {
    paperweight.paperDevBundle(libs.findVersion("minecraft").get().toString())
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
}

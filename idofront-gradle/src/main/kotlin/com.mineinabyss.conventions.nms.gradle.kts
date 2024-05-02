plugins {
    java
    io.papermc.paperweight.userdev
}

val libs = idofrontLibsRef

dependencies {
    paperweight.paperDevBundle(libs.findVersion("minecraft").get().toString())

}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

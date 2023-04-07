import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    java
}

val libs = the<LibrariesForLibs>()
val nmsExtension = project.extensions.findByName("nms") as? NmsExtension ?: project.extensions.create("nms")
val nmsDep = nmsExtension.serverVersion.orElse(libs.versions.minecraft.get()).map { "io.papermc.paper:paper-server:$it:mojang-mapped" }

repositories {
    maven("https://repo.codemc.io/repository/nms/")
}

dependencies {
    compileOnly(nmsDep)
}

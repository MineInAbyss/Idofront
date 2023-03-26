import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    java
}

val libs = the<LibrariesForLibs>()
val nmsDep = "io.papermc.paper:paper-server:${libs.versions.minecraft.get()}:mojang-mapped"

repositories {
    maven("https://repo.codemc.io/repository/nms/")
}

dependencies {
    compileOnly(nmsDep)
}

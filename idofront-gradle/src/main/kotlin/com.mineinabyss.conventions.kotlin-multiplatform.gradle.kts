import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    kotlin("multiplatform")
}

val libs = the<LibrariesForLibs>()
val jvmVersion: Int = libs.versions.jvm.get().toInt()

kotlin {
    jvmToolchain(jvmVersion)
}

plugins {
    kotlin("multiplatform")
}

repositories {
    mavenCentral()
    maven("https://repo.mineinabyss.com/releases")
}

val libs = idofrontLibsRef

val jvmVersion: Int = libs.findVersion("jvm-for-kotlin-multiplatform").get().toString().toInt()

kotlin {
    jvmToolchain(jvmVersion)
}

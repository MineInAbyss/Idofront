plugins {
    java
    kotlin("jvm")
}

val libs = idofrontLibsRef

val jvmVersion: Int = libs.findVersion("java").get().toString().toInt()
val libsKotlinVersion: String = libs.findVersion("kotlin").get().toString()

val kotlinVersion: String? by project
if (kotlinVersion != null) {
    logger.error(
        """
        Setting kotlinVersion in gradle.properties is no longer supported.
        Please remove the property and add kotlin via an alias (see https://wiki.mineinabyss.com/idofront/gradle/catalog/#plugin-aliases)
        """.trimIndent()
    )
}

repositories {
    mavenCentral()
    maven("https://repo.mineinabyss.com/releases")
}

java {
    withSourcesJar()
}

val idoExtension = getIdoExtension()
if (idoExtension.setJvmToolchain.getOrElse(true)) {
    kotlin {
        jvmToolchain(jvmVersion)
    }
}

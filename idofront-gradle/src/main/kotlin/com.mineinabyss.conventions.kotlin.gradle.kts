import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm")
}

val libs = the<LibrariesForLibs>()

val jvmVersion: Int = libs.versions.jvm.get().toInt()
val miaConventionsKotlinVersion: String = libs.versions.kotlin.get()

val kotlinVersion: String? by project
if (kotlinVersion != null) {
    logger.error(
        """
        Setting kotlinVersion in gradle.properties is no longer supported.
        Please remove the property and add kotlin via an alias (see https://wiki.mineinabyss.com/idofront/gradle/catalog/#plugin-aliases)
        """.trimIndent()
    )
}

// Let others read kotlinVersion and idofront version published with these conventions
if (kotlinVersion == null) project.ext["kotlinVersion"] = miaConventionsKotlinVersion

repositories {
    mavenCentral()
    maven("https://repo.mineinabyss.com/releases")
}

java {
    withSourcesJar()
}

kotlin {
    jvmToolchain(jvmVersion)
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "$jvmVersion"
    }
}

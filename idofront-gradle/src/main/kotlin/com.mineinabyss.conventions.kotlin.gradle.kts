import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm")
}

val libs = the<LibrariesForLibs>()

val miaConventionsKotlinVersion: String = libs.versions.kotlin.get()

val kotlinVersion: String? by project

tasks {
    kotlin {
        if (kotlinVersion != null && coreLibrariesVersion != kotlinVersion) {
            logger.error(
                """
                The version of Kotlin applied by Idofront's conventions plugin ($coreLibrariesVersion)
                is not the same as the one defined in gradle.properties ($kotlinVersion).
                
                You are likely using a different version than Idofront was built with and need to add:
                    kotlin("jvm") version "$kotlinVersion" apply false
                to your root project's plugins block.
                """.trimIndent()
            )
        }
    }
}

// Let others read kotlinVersion and idofront version published with these conventions
if (kotlinVersion == null)
    project.ext["kotlinVersion"] = miaConventionsKotlinVersion


repositories {
    mavenCentral()
    maven("https://repo.mineinabyss.com/releases")
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
}

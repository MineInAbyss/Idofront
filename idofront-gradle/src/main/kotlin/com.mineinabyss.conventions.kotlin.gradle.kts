import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.konan.properties.Properties
import java.io.FileNotFoundException

plugins {
    java
    kotlin("jvm")
}

fun loadPropertiesFromResources(propFileName: String): Properties {
    val props = Properties()
    val inputStream = javaClass.classLoader!!.getResourceAsStream(propFileName)
        ?: throw FileNotFoundException("property file '$propFileName' not found in the classpath")
    inputStream.use { props.load(it) }
    return props
}

val savedProps = loadPropertiesFromResources("mineinabyss-conventions.properties")
val miaConventionsKotlinVersion: String by savedProps

val kotlinVersion: String? by project

tasks {
    kotlin {
        if(kotlinVersion != null && coreLibrariesVersion != kotlinVersion) {
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
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
}

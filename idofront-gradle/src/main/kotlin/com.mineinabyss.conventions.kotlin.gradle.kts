import org.jetbrains.kotlin.konan.properties.Properties
import java.io.FileNotFoundException

plugins {
    java
    kotlin("jvm")
    id("com.mineinabyss.conventions.platform")
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(16))
    }
}

fun loadPropertiesFromResources(propFileName: String): Properties {
    val props = Properties()
    val inputStream = javaClass.classLoader!!.getResourceAsStream(propFileName)
        ?: throw FileNotFoundException("property file '$propFileName' not found in the classpath")
    inputStream.use { props.load(it) }
    return props
}

val savedProps = loadPropertiesFromResources("mineinabyss-conventions.properties")
val miaConventionsVersion: String by savedProps
val miaIdofrontVersion: String by savedProps
val miaConventionsKotlinVersion: String by savedProps

val kotlinVersion: String? by project
val idofrontVersion: String? by project

if (kotlinVersion != null && miaConventionsKotlinVersion != kotlinVersion)
    logger.error(
        """
        kotlinVersion property ($kotlinVersion) is not the same as the one
        applied by the Mine in Abyss conventions plugin $miaConventionsKotlinVersion.
        
        Will be using $miaConventionsKotlinVersion for Kotlin plugin and stdlib version.
        Try to remove kotlinVersion from gradle.properties or ensure you are on the same version.
        """.trimIndent()
    )

// Let others read kotlinVersion and idofront version published with these conventions
if (kotlinVersion == null)
    project.ext["kotlinVersion"] = miaConventionsKotlinVersion

if(idofrontVersion == null)
    project.ext["idofrontVersion"] = miaIdofrontVersion

repositories {
    mavenCentral()
    maven("https://repo.mineinabyss.com/releases")
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
    implementation(platform("com.mineinabyss:idofront-platform:${miaConventionsVersion}"))
}

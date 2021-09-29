import org.jetbrains.kotlin.gradle.utils.loadPropertyFromResources
import org.jetbrains.kotlin.konan.properties.Properties
import java.io.FileNotFoundException
import java.util.*

plugins {
    java
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
    id("io.github.slimjar")
    id("com.mineinabyss.conventions.platform")
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
val miaConventionsKotlinVersion: String by savedProps

val kotlinVersion: String? by project
val idofrontVersion: String? by project
val addIdofrontSlimjarDependency: String? by project

if (kotlinVersion != null && miaConventionsKotlinVersion != kotlinVersion)
    logger.error(
        """
        kotlinVersion property ($kotlinVersion) is not the same as the one
        applied by the Mine in Abyss conventions plugin $miaConventionsKotlinVersion.
        
        Will be using $miaConventionsKotlinVersion for Kotlin plugin and stdlib version.
        Try to remove kotlinVersion from gradle.properties or ensure you are on the same version.
        """.trimIndent()
    )

// Let others read kotlinVersion
if (kotlinVersion == null)
    project.ext["kotlinVersion"] = miaConventionsKotlinVersion

//TODO get kotlin version from plugin and send warning if it doesnt match specified kotlinVersion
repositories {
    mavenCentral()
    maven("https://repo.mineinabyss.com/releases")
    maven("https://repo.vshnv.tech/releases/") // Slimjar
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
    implementation("io.github.slimjar:slimjar:1.2.6")
    // Default to min version that includes slimjar dependency
    if (addIdofrontSlimjarDependency != "false")
        implementation("com.mineinabyss:idofront-slimjar:${idofrontVersion ?: "1.17.1-0.6.24"}")
    slim(platform("com.mineinabyss:idofront-gradle-platform:${miaConventionsVersion}"))
}

object TestStuff {
    val test = ""
}
abstract class GreetingTask : DefaultTask() {
    @get:Input
    abstract val greeting: Property<String>

    init {
        greeting.convention("hello from GreetingTask")
    }

    @TaskAction
    fun greet() {
        println(greeting.get())
    }
}

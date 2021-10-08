import org.jetbrains.kotlin.konan.properties.Properties
import java.io.FileNotFoundException

//TODO separate slimjar convention
plugins {
    java
    id("com.github.johnrengelman.shadow")
    id("io.github.slimjar")
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
val miaSlimjarDependencyVersion: String by savedProps

val idofrontVersion: String? by project
val addIdofrontSlimjarDependency: String? by project

repositories {
    mavenCentral()
    maven("https://repo.mineinabyss.com/releases")
    maven("https://repo.vshnv.tech/releases/") // Slimjar
}

dependencies {
    implementation("io.github.slimjar:slimjar:$miaSlimjarDependencyVersion")
    // Default to min version that includes slimjar dependency
    if (addIdofrontSlimjarDependency != "false")
        implementation("com.mineinabyss:idofront-slimjar:${idofrontVersion ?: "1.17.1-0.6.24"}")
    slim(platform("com.mineinabyss:idofront-gradle-platform:${miaConventionsVersion}"))
}

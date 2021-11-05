import org.jetbrains.kotlin.konan.properties.Properties
import java.io.FileNotFoundException

plugins {
    java
    id("de.nycode.spigot-dependency-loader")
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


repositories {
    mavenCentral()
    maven("https://repo.mineinabyss.com/releases")
}

dependencies {
    spigot(platform("com.mineinabyss:idofront-gradle-platform:${miaConventionsVersion}"))
}

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val serverVersion: String by project
val useNMS: String? by project

plugins {
    java
}

java {
    targetCompatibility = JavaVersion.VERSION_16
}

repositories {
    maven("https://repo.codemc.io/repository/nms/")
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    // Use old papermc groupId with versions below 1.17
    val (major, minor) = serverVersion.split('.').take(2).map { it.toInt() }
    val paperGroup =
        if(major == 1 && minor < 17) "com.destroystokyo.paper"
        else "io.papermc.paper"

    compileOnly("$paperGroup:paper-api:$serverVersion")
    if(useNMS.toBoolean())
        compileOnly("$paperGroup:paper:$serverVersion") // NMS
}

tasks {
    processResources {
        expand(mutableMapOf("plugin_version" to version))
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "16"
    }
}

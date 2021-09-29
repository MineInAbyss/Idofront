import java.util.Properties

// Load properties from root gradle.properties
Properties()
    .apply { load(rootDir.toPath().resolveSibling(Project.GRADLE_PROPERTIES).toFile().inputStream()) }
    .forEach { (key, value) -> project.ext["$key"] = value }

plugins {
    `kotlin-dsl`
    `maven-publish`
}

val kotlinVersion: String by project
val runNumber = System.getenv("GITHUB_RUN_NUMBER") ?: "DEV"
version = "$kotlinVersion-$runNumber"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("gradle-plugin", kotlinVersion))
    implementation(kotlin("serialization", kotlinVersion))
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.5.30")
    implementation("io.github.slimjar:gradle-plugin:1.3.0")
    implementation("gradle.plugin.com.github.jengelman.gradle.plugins:shadow:7.0.0")
    implementation(kotlin("stdlib-jdk8", kotlinVersion))
    implementation(kotlin("reflect", kotlinVersion))
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
}

gradlePlugin {
    plugins {
        register("shared-gradle") {
            displayName = "Shared gradle"
            id = "com.mineinabyss.shared-gradle"
            implementationClass = "com.mineinabyss.SharedGradlePlugin"
            description = "Shared gradle scripts for our org"
        }
    }
}

publishing {
    repositories {
        maven("https://repo.mineinabyss.com/releases") {
            credentials {
                username = project.findProperty("mineinabyssMavenUsername") as String?
                password = project.findProperty("mineinabyssMavenPassword") as String?
            }
        }
    }
//    publications {
//        register("maven", MavenPublication::class) {
//            artifactId = "shared-gradle"
//        }
//    }
}

tasks.processResources {
    expand(mutableMapOf(
        "miaConventionsVersion" to version,
        "miaConventionsKotlinVersion" to kotlinVersion))
}
tasks.publish {
    dependsOn("check")
}
tasks.build {
    dependsOn(tasks.processResources)
}

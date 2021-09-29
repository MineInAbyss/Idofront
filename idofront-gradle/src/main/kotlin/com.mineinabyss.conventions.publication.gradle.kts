plugins {
    java
    `maven-publish`
    id("org.jetbrains.dokka")
}

val runNumber: String? = System.getenv("GITHUB_RUN_NUMBER")
val runNumberDelimiter: String? by project
val addRunNumber: String? by project
val publishComponentName: String? by project

if (addRunNumber != "false" && runNumber != null)
    version = "$version${runNumberDelimiter ?: '.'}$runNumber"

java {
    withSourcesJar()
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
    publications {
        register("maven", MavenPublication::class) {
            from(components[publishComponentName ?: "java"])
        }
    }
}

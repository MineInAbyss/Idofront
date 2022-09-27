plugins {
    java
    `maven-publish`
    id("org.jetbrains.dokka")
}

val publishComponentName: String? by project
val publishArtifactId: String? by project

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
            if(publishArtifactId != null) artifactId = publishArtifactId
        }
    }
}

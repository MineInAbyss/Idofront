import org.jetbrains.kotlin.util.prefixIfNot

plugins {
    `version-catalog`
    `maven-publish`
}

val kotlinVersion: String by project
val runNumber = System.getenv("GITHUB_RUN_NUMBER") ?: "DEV"

version = "$kotlinVersion-$runNumber"

catalog {
    versionCatalog {
        from(rootProject.files("gradle/libs.versions.toml"))
        // Add aliases for all our conventions plugins
        rootProject.file("idofront-gradle/src/main/kotlin").list()?.forEach { name ->
            val id = name.removeSuffix(".gradle.kts")
            plugin(id.removePrefix("com.mineinabyss.conventions").prefixIfNot("mia"), id).version(version.toString())
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
    publications {
        create<MavenPublication>("maven") {
            artifactId = "catalog"
            from(components["versionCatalog"])
        }
    }
}

tasks.create("build") {
    dependsOn("publishToMavenLocal")
}

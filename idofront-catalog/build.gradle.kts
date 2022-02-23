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

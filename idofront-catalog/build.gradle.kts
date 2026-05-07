plugins {
    `version-catalog`
    id(miaConventions.plugins.mia.publication.get().pluginId)
}

val rootDir = isolated.rootProject.projectDirectory

catalog {
    versionCatalog {
        from(rootDir.files("gradle/libs.versions.toml"))

        // Add aliases for all our conventions plugins by copying the catalog provided by the conventions repo
        val conventions = extensions
            .getByType<VersionCatalogsExtension>()
            .named("miaConventions")
        version("mia-conventions", miaConventions.versions.mia.conventions.get())
        conventions.pluginAliases.forEach {
            plugin(it, conventions.findPlugin(it).get().get().pluginId).versionRef("mia-conventions")
        }

        // Add all idofront projects to the catalog
        rootDir.asFile.list()?.filter { it.startsWith("idofront") }?.forEach { name ->
            library(name, "com.mineinabyss:$name:$version")
        }
        bundle(
            "idofront-core", listOf(
                "idofront-commands",
                "idofront-config",
                "idofront-di",
                "idofront-fonts",
                "idofront-logging",
                "idofront-serializers",
                "idofront-text-components",
                "idofront-util",
            )
        )
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["versionCatalog"])
            artifactId = "catalog"
        }
    }
}

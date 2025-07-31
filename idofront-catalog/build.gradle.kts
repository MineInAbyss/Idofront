import org.jetbrains.kotlin.util.prefixIfNot

plugins {
    `version-catalog`
    alias(miaConventions.plugins.mia.publication)
}

catalog {
    versionCatalog {
        from(rootProject.files("gradle/libs.versions.toml"))

        // Add aliases for all our conventions plugins by copying the catalog provided by the conventions repo
        val conventions = rootProject.extensions
            .getByType<VersionCatalogsExtension>()
            .named("miaConventions")
        version("mia-conventions", miaConventions.versions.mia.conventions.get())
        conventions.pluginAliases.forEach {
            plugin(it, conventions.findPlugin(it).get().get().pluginId).versionRef("mia-conventions")
        }

        // Add all idofront projects to the catalog
        rootProject.file(".").list()?.filter { it.startsWith("idofront") }?.forEach { name ->
            library(name, "com.mineinabyss:$name:$version")
        }
        bundle(
            "idofront-core", listOf(
                "idofront-commands",
                "idofront-config",
                "idofront-di",
                "idofront-features",
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

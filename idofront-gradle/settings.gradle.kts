import java.util.*

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
            version("idofront", Properties()
                .apply { load(rootDir.toPath().resolveSibling(Project.GRADLE_PROPERTIES).toFile().inputStream()) }
                .getProperty("version"))
        }
    }
}

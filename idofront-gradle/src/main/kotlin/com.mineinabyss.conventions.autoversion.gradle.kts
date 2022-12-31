val releaseVersion: String? = System.getenv("RELEASE_VERSION")

//version = AutoVersion.getNextVersion("0.1.0")

subprojects {
    version = rootProject.version
}


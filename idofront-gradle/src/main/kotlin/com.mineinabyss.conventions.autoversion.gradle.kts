val releaseVersion: String? = System.getenv("RELEASE_VERSION")

version = (if (releaseVersion != null) {
    val (major, minor, patch) = releaseVersion.split(".")
    val (majorTarget, minorTarget) = version.toString().split(".")
    if (majorTarget == major && minorTarget == minor) {
        "$major.$minor.${patch.toInt() + 1}"
    } else {
        "$majorTarget.$minorTarget.0"
    }
} else "$version-dev")

subprojects {
    version = rootProject.version
}

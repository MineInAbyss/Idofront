val releaseVersion: String? = System.getenv("RELEASE_VERSION")

fun getNextVersion(): String {
    if (releaseVersion != null) {
        val (majorTarget, minorTarget) = version.toString().split(".")
        try {
            val (major, minor, patch) = releaseVersion.removePrefix("v").split(".")
            if (majorTarget == major && minorTarget == minor) {
                return "$major.$minor.${patch.toInt() + 1}"
            }
        } catch (_: Exception) {
        }
        return "$majorTarget.$minorTarget.0"
    } else return "$version"
}

version = getNextVersion()


subprojects {
    version = rootProject.version
}

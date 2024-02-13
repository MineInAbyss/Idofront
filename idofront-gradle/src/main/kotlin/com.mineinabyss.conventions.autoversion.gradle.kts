fun getNextVersion(): String {
    val releaseVersion: String? = System.getenv("RELEASE_VERSION")
    val branchVersion: String? = System.getenv("BRANCH_VERSION")
    val isSnapshot = System.getenv("IS_SNAPSHOT") == "true"

    if (releaseVersion == null) return "$version-dev"

    val (major, minor) = version.toString().split(".").takeIf { it.size >= 2 }
        ?: error("Version $version does not match major.minor format!")
    fun bump(bump: String?, matchPatch: String? = null) = bump
        ?.removePrefix("v")
        ?.replace(Regex("-\\w*"), "")
        ?.split(".")
        ?.takeIf { it.size > 2 && it[0] == major && it[1] == minor && (matchPatch == null || it.getOrNull(2) == matchPatch) }
        ?.lastOrNull()?.toInt()?.plus(1)
        ?: 0

    return buildString {
        val bumpedPatch = bump(releaseVersion)
        append("$major.$minor.$bumpedPatch")
        if (isSnapshot) append("-dev.${bump(branchVersion, bumpedPatch.toString())}")
    }
}

version = getNextVersion()

subprojects {
    version = rootProject.version
}

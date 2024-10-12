import java.net.URL
import java.util.*

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

tasks.register("updateIdofrontVersion") {
    doLast {
        val githubApiUrl = "https://api.github.com/repos/MineInAbyss/Idofront/releases/latest"
        val connection = URL(githubApiUrl).openConnection()
        connection.setRequestProperty("Accept", "application/vnd.github.v3+json")

        val response = connection.inputStream.bufferedReader().use { it.readText() }
        val latestVersion = "\"tag_name\":\\s*\"v?([^\"]+)\"".toRegex().find(response)?.groupValues?.get(1)

        if (latestVersion != null) {
            val gradlePropertiesFile = project.file("gradle.properties")
            val properties = Properties()

            gradlePropertiesFile.inputStream().use { properties.load(it) }
            val currentVersion = properties.getProperty("idofrontVersion")

            if (currentVersion != latestVersion) {
                properties.setProperty("idofrontVersion", latestVersion)
                gradlePropertiesFile.outputStream().use { properties.store(it, null) }
                println("Updated idofrontVersion from $currentVersion to $latestVersion")
            } else {
                println("idofrontVersion is already up to date ($currentVersion)")
            }
        } else {
            println("Failed to retrieve the latest version from GitHub")
        }
    }
}

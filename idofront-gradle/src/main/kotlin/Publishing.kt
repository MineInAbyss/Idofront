import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension

fun PublishingExtension.addMineInAbyssRepo(
    project: Project,
) {
    repositories {
        maven {
            val repo = "https://repo.mineinabyss.com/"
            setUrl(if (project.version.toString().endsWith("SNAPSHOT")) repo + "snapshots" else repo + "releases")
            credentials {
                username = project.findProperty("mineinabyssMavenUsername") as String?
                password = project.findProperty("mineinabyssMavenPassword") as String?
            }
        }
    }
}

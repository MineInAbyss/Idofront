import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.maven

fun PublishingExtension.addMineInAbyssRepo(
    project: Project,
) {
    repositories {
        maven("https://repo.mineinabyss.com/releases") {
            credentials {
                username = project.findProperty("mineinabyssMavenUsername") as String?
                password = project.findProperty("mineinabyssMavenPassword") as String?
            }
        }
    }
}

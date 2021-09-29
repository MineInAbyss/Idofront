import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.*

fun PublishingExtension.mineInAbyss(project: Project, publication: (MavenPublication.() -> Unit)? = null) {
    repositories {
        maven("https://repo.mineinabyss.com/releases") {
            credentials {
                username = project.findProperty("mineinabyssMavenUsername") as String?
                password = project.findProperty("mineinabyssMavenPassword") as String?
            }
        }
    }
    publications {
        register("maven", MavenPublication::class) {
            if (publication != null) publication()
        }
    }
}

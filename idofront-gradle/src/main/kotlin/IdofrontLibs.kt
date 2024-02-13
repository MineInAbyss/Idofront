import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

val Project.idofrontLibsRef: VersionCatalog get() = rootProject.extensions.getByType<VersionCatalogsExtension>().named("idofrontLibs")
    ?: error("idofrontLibs version catalog is not defined in settings.gradle!")

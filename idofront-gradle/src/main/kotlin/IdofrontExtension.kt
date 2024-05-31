import org.gradle.api.Project
import org.gradle.api.provider.Property

interface IdofrontExtension {
    // A configurable greeting
    val setJvmToolchain: Property<Boolean>
}

fun Project.getIdoExtension() = extensions.findByType(IdofrontExtension::class.java)
    ?: project.extensions.create("idofront", IdofrontExtension::class.java)

import org.gradle.api.provider.Property

interface NmsExtension {
    // A configurable greeting
    val serverVersion: Property<String>
}

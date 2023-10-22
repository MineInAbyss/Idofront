plugins {
    id("com.mineinabyss.conventions.kotlin.jvm")
    id("com.mineinabyss.conventions.copyjar")
    id("com.mineinabyss.conventions.papermc")
}

dependencies {
    val libs = rootProject.extensions.getByType<VersionCatalogsExtension>().named("libs")
    libs.findBundle("platform").get().get().forEach {
        implementation(it)
    }
}

copyJar {
    jarName.set("idofront-platform-$version.jar")
    excludePlatformDependencies.set(false)
}

plugins {
    id("com.mineinabyss.conventions.kotlin.jvm")
    id("com.mineinabyss.conventions.copyjar")
}

dependencies {
    val libs = rootProject.extensions.getByType<VersionCatalogsExtension>().named("libs")
    libs.findBundle("platform").get().get().forEach {
        implementation(it)
    }
}

tasks {
    shadowJar {
        archiveBaseName.set("mineinabyss")
        archiveClassifier.set("")
        archiveExtension.set("platform")
    }
}

copyJar {
    excludePlatformDependencies.set(false)
}

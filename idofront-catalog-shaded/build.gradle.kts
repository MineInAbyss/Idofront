plugins {
    id("com.mineinabyss.conventions.kotlin.jvm")
    id("com.mineinabyss.conventions.copyjar")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.nms")
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

val libs = rootProject.extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    libs.findBundle("platform").get().get().forEach {
        implementation(it)
    }

    rootProject.subprojects
        .filter { it.name.startsWith("idofront-") }
        .filter { it.name !in setOf("idofront-catalog", "idofront-catalog-shaded") }
        .forEach { implementation(project(it.path)) }
}

copyJar {
    jarName.set("idofront-$version.jar")
    excludePlatformDependencies.set(false)
}

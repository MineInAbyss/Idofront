plugins {
    id("com.mineinabyss.conventions.kotlin.jvm")
    id("com.mineinabyss.conventions.copyjar")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.nms")
}

val libs = rootProject.extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    libs.findBundle("platform").get().get().forEach {
        implementation(it)
    }

    // Shaded as dependency is SpigotMapped, so we fork it and set it to Mojang mapped
    implementation(files("../libs/anvilgui-1.10.0-SNAPSHOT.jar"))

    rootProject.subprojects
        .filter { it.name.startsWith("idofront-") }
        .filter { it.name !in setOf("idofront-catalog", "idofront-catalog-shaded") }
        .forEach { implementation(project(it.path)) }
}

copyJar {
    jarName.set("idofront-$version.jar")
    excludePlatformDependencies.set(false)
}

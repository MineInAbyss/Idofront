import me.champeau.gradle.japicmp.JapicmpTask
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

plugins {
    id(miaConventions.plugins.mia.kotlin.jvm.get().pluginId)
    id("com.mineinabyss.conventions.copyjar")
//    id(miaConventions.plugins.mia.copyjar.get().pluginId)
    id(miaConventions.plugins.mia.papermc.get().pluginId)
    id("me.champeau.gradle.japicmp") version "0.4.5"
}

repositories {
    google()
}

tasks {
    register<JapicmpTask>("checkBreakingChanges") {
        val pastReleases = isolated.rootProject.projectDirectory.file("past-releases")
        oldClasspath.from(
            pastReleases.asFile.toPath().takeIf { it.isDirectory() }?.listDirectoryEntries()?.firstOrNull()
        )
        newClasspath.from(shadowJar)
        onlyBinaryIncompatibleModified = true
        includeSynthetic = true
        ignoreMissingClasses = true
        failOnModification = true
        packageIncludes = listOf("com.mineinabyss.*")
        mdOutputFile = layout.buildDirectory.file("reports/japi.md")
    }
}

dependencies {
    libs.bundles.platform.get().forEach {
        implementation(it)
    }

    isolated.rootProject.projectDirectory.asFile.toPath().listDirectoryEntries("idofront-*")
//        .filter { it.name.startsWith("idofront-") }
        .filter { it.name !in setOf("idofront-catalog", "idofront-catalog-shaded") }
        .forEach { implementation(project(":${it.name}")) }
}

copyJar {
    jarName = "idofront-$version.jar"
    excludePlatformDependencies = false
}

paper {
    main = "com.mineinabyss.idofront.IdofrontPlugin"
    authors = listOf("Offz", "boy0000")
    load = PluginLoadOrder.STARTUP
}
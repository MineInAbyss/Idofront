import me.champeau.gradle.japicmp.JapicmpTask
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries

plugins {
    alias(miaConventions.plugins.mia.kotlin.jvm)
    alias(miaConventions.plugins.mia.copyjar)
    alias(miaConventions.plugins.mia.papermc)
    id("me.champeau.gradle.japicmp") version "0.4.5"
}

val libs = rootProject.extensions.getByType<VersionCatalogsExtension>().named("libs")

repositories {
    google()
}

tasks {
    register<JapicmpTask>("checkBreakingChanges") {
        oldClasspath.from(
            rootProject.file("past-releases").toPath().takeIf { it.isDirectory() }?.listDirectoryEntries()?.firstOrNull()
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

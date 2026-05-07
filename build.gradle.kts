plugins {
    java
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.dependencyversions)
    alias(libs.plugins.version.catalog.update)
    alias(miaConventions.plugins.mia.docs)
}

tasks {
    updateDaemonJvm {
        languageVersion = JavaLanguageVersion.of(25)
        vendor = JvmVendorSpec.JETBRAINS
    }
    dependencyUpdates {
        rejectVersionIf {
            isNonStable(candidate.version)
        }
    }

    val versions = libs.versions
    val minecraftVersion = versions.minecraft.get()
    val kotlinVersion = versions.kotlin.get()
    val javaVersion = versions.java.get()

    register("versionInfo") {
        val outputFile = file("build/versions.md")
        inputs.properties("minecraft" to minecraftVersion, "kotlin" to kotlinVersion, "java" to javaVersion)
        outputs.file(outputFile)
        doLast {
            outputFile.writeText(
                """
                ### Built for
                | Minecraft | `${minecraftVersion.substringBefore("-R")}` |
                |-----------|--|
                | Kotlin | `$kotlinVersion` |
                | Java | `$javaVersion` |
                """.trimIndent()
            )
        }
    }
}

fun isNonStable(version: String): Boolean {
    val unstableKeywords = listOf(
        "-beta",
        "-rc",
        "-alpha",
    )

    return unstableKeywords.any { version.contains(it, ignoreCase = true) }
}

versionCatalogUpdate {
    keep {
        keepUnusedVersions = true
    }
}

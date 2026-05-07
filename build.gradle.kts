plugins {
    java
//    alias(idofrontLibs.plugins.kotlin.jvm) apply false
    alias(idofrontLibs.plugins.mia.docs)
}

tasks {
    updateDaemonJvm {
        languageVersion = JavaLanguageVersion.of(25)
        vendor = JvmVendorSpec.JETBRAINS
    }

    val versions = idofrontLibs.versions
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

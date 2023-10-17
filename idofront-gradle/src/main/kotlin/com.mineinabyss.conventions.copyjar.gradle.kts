plugins {
    java
    com.github.johnrengelman.shadow
}

val pluginPath = project.findProperty("plugin_path") as? String

interface CopyJarExtension {
    // A configurable greeting
    val destPath: Property<String>

    val jarName: Property<String>

    val excludePlatformDependencies: Property<Boolean>
}

val copyJar = project.extensions.create<CopyJarExtension>("copyJar")

if (pluginPath != null) {
    tasks {
        register<Copy>("copyJar") {
            doNotTrackState("Overwrites the plugin jar to allow for easier reloading")
            val dest = copyJar.destPath.orNull ?: pluginPath
            val jarName = copyJar.jarName.orNull ?: "${project.name}-${project.version}.jar"

            from(findByName("reobfJar") ?: findByName("shadowJar") ?: findByName("jar"))
            into(dest)
            doLast {
                println("Copied to plugin directory $dest")
            }
            rename(".*\\.jar", jarName)
        }

        named<DefaultTask>("build") {
            dependsOn("copyJar")
        }
    }
}

tasks.assemble {
    if (copyJar.excludePlatformDependencies.getOrElse(true)) {
        configurations {
            findByName("runtimeClasspath")?.apply {
                val libs = rootProject.extensions.getByType<VersionCatalogsExtension>().named("libs")
                val deps = libs.findBundle("platform").get().get()
                deps.forEach {
                    exclude(group = it.group, module = it.name)
                }
                println("Excluding ${deps.size} dependencies from runtimeClasspath that are present in mineinabyss.platform")
            }
            runtimeClasspath {
                exclude(group = "org.jetbrains.kotlin")
                exclude(group = "org.jetbrains.kotlinx")
            }
        }
    }
}

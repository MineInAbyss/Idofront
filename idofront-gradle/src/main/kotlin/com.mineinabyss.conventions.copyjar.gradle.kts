plugins {
    java
    com.github.johnrengelman.shadow
}

val pluginPath = project.findProperty("plugin_path") as? String

interface CopyJarExtension {
    // A configurable greeting
    val destPath: Property<String>

    val jarName: Property<String>
}

val copyJar = project.extensions.create<CopyJarExtension>("copyJar")

if (pluginPath != null) {
    tasks {
        register<Copy>("copyJar") {
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
//
//class CopyJar : DefaultTask() {
//    @get:Input
//    val destPath: Property<String> = project.objects.property<String>()
//        .convention(pluginPath as String)
//
//    @get:Input
//    val jarName: Property<String> = project.objects.property<String>()
//
//
//    @TaskAction
//    fun copyJar() {
//        val jar = project.tasks.findByName("reobfJar") ?: project.tasks.findByName("shadowJar") ?: project.tasks.findByName("jar")
//        project.copy {
//            from(jar)
//            into(destPath.get())
//        }
//        doLast {
//            println("Copied to plugin directory ${destPath.get()}")
//        }
//    }
//}

plugins {
    // Use idofrontLibs for these in other projects
    id(idofrontLibs.plugins.mia.kotlin.jvm.get().pluginId)
    id(idofrontLibs.plugins.mia.papermc.get().pluginId)
    // TODO only copy when running a specific task
//    id(idofrontLibs.plugins.mia.copyjar.get().pluginId)
    alias(idofrontLibs.plugins.kotlinx.serialization)
}

dependencies {
    // Use idofrontLibs for these in other projects
    compileOnly(projects.idofrontCommands)
    compileOnly(projects.idofrontConfig)
    compileOnly(projects.idofrontLogging)
}

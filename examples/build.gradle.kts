plugins {
    // Use idofrontLibs for these in other projects
    id(miaLibs.plugins.mia.kotlin.jvm.get().pluginId)
    id(miaLibs.plugins.mia.papermc.get().pluginId)
    // TODO only copy when running a specific task
//    id(miaLibs.plugins.mia.copyjar.get().pluginId)
    alias(miaLibs.plugins.kotlinx.serialization)
}

dependencies {
    // Use idofrontLibs for these in other projects
    compileOnly(projects.idofrontCommands)
    compileOnly(projects.idofrontConfig)
    compileOnly(projects.idofrontLogging)
}

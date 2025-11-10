plugins {
    // Use idofrontLibs for these in other projects
    id(miaConventions.plugins.mia.kotlin.jvm.get().pluginId)
    id(miaConventions.plugins.mia.papermc.get().pluginId)
    // TODO only copy when running a specific task
//    id(miaConventions.plugins.mia.copyjar.get().pluginId)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    // Use idofrontLibs for these in other projects
    compileOnly(projects.idofrontCommands)
    compileOnly(projects.idofrontConfig)
    compileOnly(projects.idofrontLogging)
}

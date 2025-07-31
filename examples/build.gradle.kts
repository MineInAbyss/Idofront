plugins {
    // Use idofrontLibs for these in other projects
    alias(miaConventions.plugins.mia.kotlin.jvm)
    alias(miaConventions.plugins.mia.papermc)
    // TODO only copy when running a specific task
//    alias(miaConventions.plugins.mia.copyjar)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    // Use idofrontLibs for these in other projects
    implementation(projects.idofrontCommands)
    implementation(projects.idofrontConfig)
    implementation(projects.idofrontLogging)
}

plugins {
    id(miaConventions.plugins.mia.kotlin.jvm.get().pluginId)
    id(miaConventions.plugins.mia.papermc.get().pluginId)
    id(miaConventions.plugins.mia.publication.get().pluginId)
}

dependencies {
    implementation(projects.idofrontLogging)
    implementation(projects.idofrontTextComponents)
    implementation(libs.minecraft.mccoroutine)
    implementation(libs.kotlinx.coroutines)
}

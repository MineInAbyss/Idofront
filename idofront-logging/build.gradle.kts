plugins {
    id(miaConventions.plugins.mia.kotlin.jvm.get().pluginId)
    id(miaConventions.plugins.mia.papermc.get().pluginId)
    id(miaConventions.plugins.mia.publication.get().pluginId)
}

dependencies {
    implementation(projects.idofrontTextComponents)
    implementation(projects.idofrontDi)
    api(libs.kermit)
}

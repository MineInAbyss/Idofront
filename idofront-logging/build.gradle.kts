plugins {
    id(miaLibs.plugins.mia.kotlin.jvm.get().pluginId)
    id(miaLibs.plugins.mia.papermc.get().pluginId)
    id(miaLibs.plugins.mia.publication.get().pluginId)
}

dependencies {
    implementation(projects.idofrontTextComponents)
    api(miaLibs.kermit)
}

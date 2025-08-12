plugins {
    id(miaConventions.plugins.mia.kotlin.jvm.get().pluginId)
    id(miaConventions.plugins.mia.papermc.get().pluginId)
    id(miaConventions.plugins.mia.nms.get().pluginId)
    id(miaConventions.plugins.mia.publication.get().pluginId)
    alias(libs.plugins.kotlinx.serialization)
}

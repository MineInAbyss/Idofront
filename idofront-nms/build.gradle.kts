plugins {
    id(miaLibs.plugins.mia.kotlin.jvm.get().pluginId)
    id(miaLibs.plugins.mia.papermc.get().pluginId)
    id(miaLibs.plugins.mia.nms.get().pluginId)
    id(miaLibs.plugins.mia.publication.get().pluginId)
    alias(miaLibs.plugins.kotlinx.serialization)
}

plugins {
    id(miaConventions.plugins.mia.kotlin.jvm.get().pluginId)
    id(miaConventions.plugins.mia.publication.get().pluginId)
    id(miaConventions.plugins.mia.testing.get().pluginId)
}
dependencies {
    testImplementation(kotlin("test"))
}

plugins {
    id(miaConventions.plugins.mia.kotlin.jvm.get().pluginId)
    id(miaConventions.plugins.mia.publication.get().pluginId)
    id(miaConventions.plugins.mia.testing.get().pluginId)
}
dependencies {
    implementation("org.kodein.di:kodein-di:7.30.0")
    testImplementation(kotlin("test"))
}

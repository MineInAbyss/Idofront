plugins {
    alias(miaConventions.plugins.mia.kotlin.jvm)
    alias(miaConventions.plugins.mia.publication)
    alias(miaConventions.plugins.mia.testing)
}
dependencies {
    testImplementation(kotlin("test"))
}

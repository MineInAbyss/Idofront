plugins {
    alias(miaConventions.plugins.mia.kotlin.jvm)
    alias(miaConventions.plugins.mia.papermc)
    alias(miaConventions.plugins.mia.publication)
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    compileOnly(libs.kotlinx.serialization.json)
}

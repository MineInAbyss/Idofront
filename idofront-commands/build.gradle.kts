plugins {
    alias(miaConventions.plugins.mia.kotlin.jvm)
    alias(miaConventions.plugins.mia.papermc)
    alias(miaConventions.plugins.mia.publication)
}

dependencies {
    implementation(projects.idofrontLogging)
    implementation(projects.idofrontTextComponents)
    implementation(libs.minecraft.mccoroutine)
    implementation(libs.kotlinx.coroutines)
}

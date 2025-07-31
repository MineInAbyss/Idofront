plugins {
    alias(miaConventions.plugins.mia.kotlin.jvm)
    alias(miaConventions.plugins.mia.papermc)
    alias(miaConventions.plugins.mia.publication)
}

dependencies {
    implementation(projects.idofrontTextComponents)
    implementation(projects.idofrontDi)
    api(libs.kermit)
}

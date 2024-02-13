val libs = idofrontLibsRef

plugins {
    java
}

dependencies {
    // Junit
    testImplementation(platform(libs.findLibrary("junit.bom").get()))
    testImplementation(libs.findLibrary("junit.jupiter").get())

    // Other test libs
    testImplementation(libs.findLibrary("kotest.runner.junit5").get())
    testImplementation(libs.findLibrary("kotest.property").get())
    testImplementation(libs.findLibrary("mockk").get())
}

tasks {
    test {
        useJUnitPlatform()
    }
}

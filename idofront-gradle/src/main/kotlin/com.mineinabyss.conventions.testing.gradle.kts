import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
    id("com.mineinabyss.conventions.kotlin")
    java
}

dependencies {
    // Junit
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)

    // Other test libs
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.property)
    testImplementation(libs.mockk)
}

tasks {
    test {
        useJUnitPlatform()
    }
}

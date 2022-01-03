import Com_mineinabyss_conventions_platform_gradle.Deps

plugins {
    id("com.mineinabyss.conventions.kotlin")
    java
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.8.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.kotest:kotest-runner-junit5:5.0.2")
    testImplementation("io.kotest:kotest-property:5.0.2")
    testImplementation("io.mockk:mockk:1.12.1")

    testImplementation(Deps.koin.test)
    testImplementation(Deps.koin.`test-junit5`)
}

tasks {
    test {
        useJUnitPlatform()
    }
}

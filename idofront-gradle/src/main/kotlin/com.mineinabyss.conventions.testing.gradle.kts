plugins {
    java
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.7.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.kotest:kotest-runner-junit5:4.6.1")
    testImplementation("io.kotest:kotest-property:4.6.1")
    testImplementation("io.mockk:mockk:1.12.1")
}

tasks {
    test {
        useJUnitPlatform()
    }
}

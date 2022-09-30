plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.publication")
}

dependencies {
    api("net.kyori:adventure-api:4.11.0")
    api("net.kyori:adventure-text-minimessage:4.11.0")
    api("net.kyori:adventure-text-serializer-plain:4.11.0")
    api("net.kyori:adventure-text-serializer-legacy:4.11.0")
    api("io.github.microutils:kotlin-logging-jvm:3.0.0")
    api("org.tinylog:tinylog-api:2.5.0")
    implementation(project(":idofront-text-components"))
}

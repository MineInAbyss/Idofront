plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.publication")
    kotlin("plugin.serialization")
}

dependencies {
    //TODO remove next slimjar update
    compileOnly("io.github.slimjar:slimjar:1.2.4")
    api(project(":"))
}

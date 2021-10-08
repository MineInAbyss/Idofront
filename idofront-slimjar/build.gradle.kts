val slimjarDependencyVersion: String by project

plugins {
    id("com.mineinabyss.conventions.kotlin")
    id("com.mineinabyss.conventions.papermc")
    id("com.mineinabyss.conventions.publication")
}

repositories {
    maven("https://repo.vshnv.tech/releases/") // Slimjar
}

dependencies {
    compileOnly("io.github.slimjar:slimjar:$slimjarDependencyVersion")
}

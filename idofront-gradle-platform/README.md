[![Java CI with Gradle](https://github.com/MineInAbyss/KotlinSpice/actions/workflows/gradleci.yml/badge.svg)](https://github.com/MineInAbyss/KotlinSpice/actions/workflows/gradleci.yml)
[![Package](https://badgen.net/maven/v/metadata-url/repo.mineinabyss.com/releases/com/mineinabyss/kotlinspice/maven-metadata.xml)](https://repo.mineinabyss.com/releases/com/mineinabyss/kotlinspice)

# KotlinSpice
A gradle Java platform for common libraries for our Minecraft plugins.

We are no longer using it as an additional spigot plugin, instead we download these dependencies dynamically using [Slimjar](https://github.com/SlimJar/slimjar). 

## Usage

Updated wiki coming soon(tm).

### Gradle

```groovy
repositories {
    maven  { url 'https://repo.mineinabyss.com/releases' }
}

dependencies {
    compileOnly platform("com.mineinabyss:kotlinspice:$kotlinVersion+")
    //Add optional deps without specifying a version!
    //(The appropriate repo must still be provided for them)
    compileOnly "com.github.okkero:skedule"
    compileOnly "de.erethon:headlib"
}
```


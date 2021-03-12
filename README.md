![CI](https://github.com/MineInAbyss/Idofront/workflows/Java%20CI/badge.svg) 
[![Package](https://badgen.net/maven/v/metadata-url/repo.mineinabyss.com/releases/com/mineinabyss/idofront/maven-metadata.xml)](https://repo.mineinabyss.com/releases/com/mineinabyss/idofront)
[![Wiki](https://badgen.net/badge/color/Project%20Wiki/purple?icon=wiki&label)](https://github.com/MineInAbyss/Idofront/wiki)
[![Contribute](https://shields.io/badge/Contribute-e57be5?logo=github%20sponsors&style=flat&logoColor=white)](https://github.com/MineInAbyss/MineInAbyss/wiki/Setup-and-Contribution-Guide)

# Idofront

Idofront is a repository, shadowed by many of our plugins, which contains many useful helper functions. Some are exclusive in usefulness to code written in Kotlin.

This project is really new and not intended to be used by anyone for their own plugins yet! Don't expect any consistency or good code for now.

### Features

##### Check out the [wiki](https://github.com/MineInAbyss/Idofront/wiki)

- A clean command DSL for kotlin (May change a lot in the near future). See implementation in our plugins, notably [Mobzy](https://github.com/MineInAbyss/Mobzy/blob/master/src/main/java/com/mineinabyss/mobzy/MobzyCommands.kt).
- Custom ktx.serialization serializers for many bukkit classes.
- Many helper functions to reduce boilerplate code, such as:
    - `String.color()` to translate color codes.
    - Quick ItemStack manipulation:
        ```kotlin
        itemStack.editItemMeta{
            isUnbreakable = true
            setDisplayName("Custom name")
        }
        ```
- Destructure functions:
    ```kotlin
    val (x, y, z, world) = location  
    ```

##### Coming soon(tm):

- Config management powered by ktx.serialization (currently experimental)
- Easy custom recipe registration.

### Setup

#### Dependencies
- Use [PDM](https://github.com/knightzmc/pdm/) to auto download Kotlin (and kotlinx.serialization if you are using it).
- OR: Manage [shading](https://imperceptiblethoughts.com/shadow/) shading these libs into your jar. Shading Kotlin can cause hard-to-find errors if two different versions are present during runtime!
- OR: Depend on [KotlinSpice](https://github.com/MineInAbyss/KotlinSpice) in your plugin config, and request users to download
  the plugin to their server.

#### Gradle

```groovy
repositories {
    maven  { url 'https://repo.mineinabyss.com/releases' }
}

dependencies {
    implementation 'com.mineinabyss:idofront:<version>'
    // Or use for idofront + extra NMS stuff
    implementation 'com.mineinabyss:idofront-nms:<version>'
}
```

#### Shading

Lastly, shade Idofront (ex with gradle's shadowJar plugin, or the maven equivalent). It is recommended that you relocate the jar into a unique package in order to avoid problems when different plugins are using different versions of Idofront.

Once pdm supports relocation, we recommend just using that!

```groovy
shadowJar {
    relocate 'com.mineinabyss.idofront', "${project.group}.${project.name}.idofront".toLowerCase()

    minimize()
}
```

Minimize will only shade classes you are using and does not seem to cause problems with Idofront.

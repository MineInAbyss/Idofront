<div align="center">

# Idofront
![CI](https://github.com/MineInAbyss/Idofront/workflows/Java%20CI/badge.svg) 
[![Package](https://img.shields.io/maven-metadata/v?metadataUrl=https://repo.mineinabyss.com/releases/com/mineinabyss/idofront-util/maven-metadata.xml)](https://repo.mineinabyss.com/#/releases/com/mineinabyss/idofront-util)
[![Wiki](https://img.shields.io/badge/-Project%20Wiki-blueviolet?logo=Wikipedia&labelColor=gray)](https://wiki.mineinabyss.com/idofront)
[![Contribute](https://shields.io/badge/Contribute-e57be5?logo=github%20sponsors&style=flat&logoColor=white)](https://github.com/MineInAbyss/MineInAbyss/wiki/Setup-and-Contribution-Guide)
</div>


Idofront is a set of modules we share between plugins. It includes helpful Minecraft extensions, gradle conventions, and more.

# Modules

## `idofront`
Many helper functions for Spigot, including:
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

**Check out the [wiki](https://github.com/MineInAbyss/Idofront/wiki)**

## [`idofront-nms`](https://github.com/MineInAbyss/Idofront/tree/master/idofront-nms)
TypeAliases for many NMS classes, extension functions to make upgrading between versions less of a pain. We only support the latest Minecraft version.

## [`idofront-platform`](https://github.com/MineInAbyss/Idofront/tree/master/idofront-platform)
Java platform for enforcing dependency versions.

## [`idofront-gradle`](https://github.com/MineInAbyss/Idofront/tree/master/idofront-gradle)
Precompiled Kotlin convention plugins to share build logic.

<div align="center">

# Idofront
![CI](https://github.com/MineInAbyss/Idofront/workflows/Java%20CI/badge.svg) 
[![Package](https://img.shields.io/maven-metadata/v?metadataUrl=https://repo.mineinabyss.com/releases/com/mineinabyss/idofront-util/maven-metadata.xml)](https://repo.mineinabyss.com/#/releases/com/mineinabyss/idofront-util)
[![Wiki](https://img.shields.io/badge/-Project%20Wiki-blueviolet?logo=Wikipedia&labelColor=gray)](https://wiki.mineinabyss.com/idofront)
[![Contribute](https://shields.io/badge/Contribute-e57be5?logo=github%20sponsors&style=flat&logoColor=white)](https://github.com/MineInAbyss/MineInAbyss/wiki/Setup-and-Contribution-Guide)
</div>


Idofront is a set of modules we share between plugins. It includes helpful Minecraft extensions, gradle conventions, and more.

# Modules

**Check out the [wiki](https://github.com/MineInAbyss/Idofront/wiki) for full usage guides**

[`idofront-autoscan`](https://github.com/MineInAbyss/Idofront/tree/master/idofront-autoscan) -
Helper functions for finding annotated classes at runtime, we use it to register things at startup.

[`idofront-catalog`](https://github.com/MineInAbyss/Idofront/tree/master/idofront-catalog) -
Gradle [version catalog](https://docs.gradle.org/current/userguide/platforms.html#sub:version-catalog) containing our commonly used dependencies, including all idofront projects.

[`idofront-catalog-shaded`](https://github.com/MineInAbyss/Idofront/tree/master/idofront-catalog-shaded) -
Packaged version of our catalog. Used with our platform loader to load dependencies at runtime.

[`idofront-config`](https://github.com/MineInAbyss/Idofront/tree/master/idofront-config) -
Simple config system using kotlinx.serialization. Supports yaml, json, and more.

[`idofront-fonts`](https://github.com/MineInAbyss/Idofront/tree/master/idofront-fonts) -
Font related helper functions, including (negative) spacing.

[`idofront-gradle`](https://github.com/MineInAbyss/Idofront/tree/master/idofront-gradle) -
Gradle plugins to share build logic, including using NMS with mappings, and publishing to our maven repo.

[`idofront-logging`](https://github.com/MineInAbyss/Idofront/tree/master/idofront-logging) -
Super simple logging functions with MiniMessage support.

[`idofront-nms`](https://github.com/MineInAbyss/Idofront/tree/master/idofront-nms) -
TypeAliases and `toNMS()`, `toBukkit()` functions for many NMS classes

[`idofront-platform-loader`](https://github.com/MineInAbyss/Idofront/tree/master/idofront-platform-loader) -
Loads dependencies from a jar file, isolating them from other plugins.

[`idofront-serializers`](https://github.com/MineInAbyss/Idofront/tree/master/idofront-serializers) -
Config-centric serializers for many Bukkit classes for kotlinx.serialization, including ItemStack, Recipes, or Components (via MiniMessage.)

[`idofront-text-components`](https://github.com/MineInAbyss/Idofront/tree/master/idofront-text-components) -
Helper functions for adventure `Component`s

[`idofront-util`](https://github.com/MineInAbyss/Idofront/tree/master/idofront-util) -
General utilities like destructure functions, plugin load helpers, or operator functions for Vector and Location.

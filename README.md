<div align="center">

# Idofront
[![Publish Packages](https://github.com/MineInAbyss/Idofront/actions/workflows/publish-packages.yml/badge.svg)](https://github.com/MineInAbyss/Idofront/actions/workflows/publish-packages.yml)
[![Package](https://img.shields.io/maven-metadata/v?metadataUrl=https://repo.mineinabyss.com/releases/com/mineinabyss/idofront-util/maven-metadata.xml)](https://repo.mineinabyss.com/#/releases/com/mineinabyss/idofront-util)
[![Wiki](https://img.shields.io/badge/-Project%20Wiki-blueviolet?logo=Wikipedia&labelColor=gray)](https://wiki.mineinabyss.com/idofront)
[![Contribute](https://shields.io/badge/Contribute-e57be5?logo=github%20sponsors&style=flat&logoColor=white)](https://github.com/MineInAbyss/MineInAbyss/wiki/Setup-and-Contribution-Guide)
</div>


Idofront is a set of modules we share between plugins. It includes helpful Minecraft extensions, gradle conventions, and more.

# Modules

**Click on a module to see its wiki page!**

**`idofront-autoscan`** -
Helper functions for finding annotated classes at runtime, we use it to register things at startup.

[**`idofront-catalog`**](https://github.com/MineInAbyss/Idofront/tree/master/idofront-catalog) -
Gradle [version catalog](https://docs.gradle.org/current/userguide/platforms.html#sub:version-catalog) containing our commonly used dependencies, including all idofront projects.

[**`idofront-catalog-shaded`**](https://wiki.mineinabyss.com/idofront/platforms/) -
Packaged version of our catalog. Used with our platform loader to load dependencies at runtime.

[**`idofront-commands`**](https://wiki.mineinabyss.com/idofront/command-dsl/) -
A DSL for quickly building Minecraft commands.

**`idofront-config`** -
Simple config system using kotlinx.serialization. Supports yaml, json, and more.

**`idofront-fonts`** -
Font related helper functions, including (negative) spacing.

[**`idofront-gradle`**](https://github.com/MineInAbyss/Idofront/tree/master/idofront-gradle) -
Gradle plugins to share build logic, including using NMS with mappings, and publishing to our maven repo.

**`idofront-logging`** -
Super simple logging functions with MiniMessage support.

[**`idofront-nms`**](https://wiki.mineinabyss.com/idofront/nms/) -
TypeAliases and `toNMS()`, `toBukkit()` functions for many NMS classes

[**`idofront-platform-loader`**](https://wiki.mineinabyss.com/idofront/platforms/) -
Loads dependencies from a jar file, isolating them from other plugins.

[**`idofront-serializers`**](https://wiki.mineinabyss.com/idofront/serialization/) -
Config-centric serializers for many Bukkit classes for kotlinx.serialization, including ItemStack, Recipes, or Components (via MiniMessage.)

**`idofront-text-components`** -
Helper functions for adventure `Component`s

**`idofront-util`** -
General utilities like destructure functions, plugin load helpers, or operator functions for Vector and Location.

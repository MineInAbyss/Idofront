<div align="center">

# Idofront
[![Package](https://img.shields.io/maven-metadata/v?metadataUrl=https://repo.mineinabyss.com/releases/com/mineinabyss/idofront-util/maven-metadata.xml)](https://repo.mineinabyss.com/#/releases/com/mineinabyss/idofront-util)
[![Wiki](https://img.shields.io/badge/-Project%20Wiki-blueviolet?logo=Wikipedia&labelColor=gray)](https://wiki.mineinabyss.com/idofront)
[![Contribute](https://shields.io/badge/Contribute-e57be5?logo=github%20sponsors&style=flat&logoColor=white)](https://wiki.mineinabyss.com/contributing/)
</div>

Idofront is a set of modules we share between plugins. It includes helpful Minecraft extensions, gradle conventions, and more.

# Modules

**Click on a module to see its wiki page!**

**`idofront-autoscan`** -
Helper functions for finding annotated classes at runtime, we use it to register things at startup.

[**`idofront-catalog`**](https://wiki.mineinabyss.com/idofront/gradle/catalog/) -
Gradle [version catalog](https://docs.gradle.org/current/userguide/platforms.html#sub:version-catalog) containing our commonly used dependencies, including all idofront projects.

[**`idofront-catalog-shaded`**](https://wiki.mineinabyss.com/idofront/platforms/) -
A Paper plugin with all the dependencies shaded, intended to be used by our other plugins using Paper's `join-classpath` option.

[**`idofront-commands`**](https://wiki.mineinabyss.com/idofront/command-dsl/) -
A DSL for quickly building Minecraft commands.

[**`idofront-config`**](https://wiki.mineinabyss.com/idofront/config/) -
Simple config system using kotlinx.serialization. Supports yaml, json, and more.

**`idofront-fonts`** -
Font related helper functions, including (negative) spacing.

[**`idofront-gradle`**](https://wiki.mineinabyss.com/idofront/gradle/plugins/) -
Gradle plugins to share build logic, including using NMS with mappings, and publishing to our maven repo.

**`idofront-logging`** -
Super simple logging functions with MiniMessage support.

[**`idofront-nms`**](https://wiki.mineinabyss.com/idofront/nms/) -
TypeAliases and `toNMS()`, `toBukkit()` functions for many NMS classes

[**`idofront-serializers`**](https://wiki.mineinabyss.com/idofront/serialization/) -
Config-centric serializers for many Bukkit classes for kotlinx.serialization, including ItemStack, Recipes, or Components (via MiniMessage.)

**`idofront-text-components`** -
Helper functions for adventure `Component`s

[**`idofront-util`**](https://wiki.mineinabyss.com/idofront/util/) -
General utilities like destructure functions, plugin load helpers, or operator functions for Vector and Location.

# Reference material

- This repo seems interesting to borrow from as a setup for conventions plugins: https://github.com/huanshankeji/gradle-common
- https://github.com/jjohannes/gradle-demos/tree/main

<div align="center">

# Idofront
[![Package](https://img.shields.io/maven-metadata/v?metadataUrl=https://repo.mineinabyss.com/releases/com/mineinabyss/idofront-util/maven-metadata.xml)](https://repo.mineinabyss.com/#/releases/com/mineinabyss/idofront-util)
[![Wiki](https://img.shields.io/badge/-Project%20Wiki-blueviolet?logo=Wikipedia&labelColor=gray)](https://docs.mineinabyss.com/idofront)
[![Contribute](https://shields.io/badge/Contribute-e57be5?logo=github%20sponsors&style=flat&logoColor=white)](https://mineinabyss.com/contributing)
</div>

Idofront is a set of modules we share between plugins. It includes helpful Minecraft extensions, common dependencies, and more. We have a separate project for [gradle conventions](https://github.com/MineInAbyss/gradle-conventions), which Idofront references too.

## Usage

See the [examples](examples) module for example usage of each module (WIP). You may manually add dependencies as shown
below, or use our version catalog, or depend on Idofront as a platform. See [Installation](docs/setup/installation.md)
for
more info.

```kotlin
implementation("com.mineinabyss:idofront-commands:$idofrontVersion")
```

# Modules

- [**`catalog`**](https://wiki.mineinabyss.com/idofront/gradle/catalog/) -
Gradle [version catalog](https://docs.gradle.org/current/userguide/platforms.html#sub:version-catalog) containing our commonly used dependencies, including all idofront projects.

- [**`catalog-shaded`**](https://wiki.mineinabyss.com/idofront/platforms/) -
A Paper plugin with all the dependencies shaded, intended to be used by our other plugins using Paper's `join-classpath` option.

- [**`commands`**](https://wiki.mineinabyss.com/idofront/command-dsl/) -
A DSL for quickly building Minecraft commands.

- [**`config`**](https://wiki.mineinabyss.com/idofront/config/) -
Simple config system using kotlinx.serialization. Supports yaml, json, and more.

- **`features`** - Helper classes for splitting plugins into features that can be enabled or disabled.

- **`fonts`** -
Font related helper functions, including (negative) spacing.

- **`logging`** -
Super simple logging functions with MiniMessage support.

- [**`nms`**](https://wiki.mineinabyss.com/idofront/nms/) -
TypeAliases and `toNMS()`, `toBukkit()` functions for many NMS classes

- [**`serializers`**](https://wiki.mineinabyss.com/idofront/serialization/) -
Config-centric serializers for many Bukkit classes for kotlinx.serialization, including ItemStack, Recipes, or Components (via MiniMessage.)

- **`text-components`** -
Helper functions for adventure `Component`s

- [**`util`**](https://wiki.mineinabyss.com/idofront/util/) -
General utilities like destructure functions, plugin load helpers, or operator functions for Vector and Location.

# Reference material

- This repo seems interesting to borrow from as a setup for conventions plugins: https://github.com/huanshankeji/gradle-common
- https://github.com/jjohannes/gradle-demos/tree/main

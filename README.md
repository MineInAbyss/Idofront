<div align="center">

# Idofront
[![Package](https://img.shields.io/maven-metadata/v?metadataUrl=https://repo.mineinabyss.com/releases/com/mineinabyss/idofront-util/maven-metadata.xml)](https://repo.mineinabyss.com/#/releases/com/mineinabyss/idofront-util)
[![Wiki](https://img.shields.io/badge/-Project%20Wiki-blueviolet?logo=Wikipedia&labelColor=gray)](https://wiki.mineinabyss.com/idofront)
[![Contribute](https://shields.io/badge/Contribute-e57be5?logo=github%20sponsors&style=flat&logoColor=white)](https://wiki.mineinabyss.com/contributing/)
</div>

Idofront is a set of modules we share between plugins. It includes helpful Minecraft extensions, common dependencies, and more. We have a separate project for [gradle conventions](https://github.com/MineInAbyss/gradle-conventions), which Idofront references too.

## Quickstart

### Idofront dependencies

You may shade Idofront's modules into your plugin as you wish, just be sure you also provide Kotlin (preferably the same version we use), and that you do *not* include the Idofront plugin in your classpath. Ex:

```kotlin
implementation("com.mineinabyss:idofront-commands:$idofrontVersion")
```

We also provide a gradle version catalog to let you reference them more easily (see below.)

### Idofront platform

For Mine in Abyss, we use Idofront to ensure our plugins use the same versions of each dependency, included in a dedicated Idofront plugin. Our plugins specify a single `idofrontVersion` property which includes common dependencies, Idofront's utilities, and gradle conventions plugins in a version catalog.

#### Add idofrontVersion to your gradle.properties

```properties
idofrontVersion=...
```

#### Configure your settings.gradle

```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.mineinabyss.com/releases")
    }
}

dependencyResolutionManagement {
    val idofrontVersion: String by settings
    
    repositories {
        maven("https://repo.mineinabyss.com/releases")
    }

    versionCatalogs {
        create("idofrontLibs").from("com.mineinabyss:catalog:$idofrontVersion")
    }
}
```

#### Configure your build.gradle

```kotlin
plugins {
    alias(idofrontLibs.plugins.mia.kotlin.jvm)
    alias(idofrontLibs.plugins.mia.copyjar)
    alias(idofrontLibs.plugins.mia.papermc)
    alias(idofrontLibs.plugins.mia.nms)
    // ...
}

dependencies {
    compileOnly(idofrontLibs.bundles.idofront.core)
    compileOnly(idofrontLibs.kotlinx.serialization.json)
    // ...
}
```

#### Updating

Run `gradle updateIdofrontVersion`

# Modules

**Click on a module to see its wiki page!**

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

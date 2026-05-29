# Installation

## Include in your plugins

You may shade Idofront's modules into your plugin as you wish, just be sure you also provide Kotlin (preferably the same
version we use), and that you do *not* include the Idofront plugin in your classpath. Ex:

```kotlin
implementation("com.mineinabyss:idofront-commands:$idofrontVersion")
```

We also provide a gradle version catalog to let you reference them more easily (see below.)

## Idofront platform

For Mine in Abyss, we use Idofront to ensure our plugins use the same versions of each dependency, included in a
dedicated Idofront plugin. Our plugins specify a single `idofrontVersion` property which includes common dependencies,
Idofront's utilities, and gradle conventions plugins in a version catalog.

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
    val miaLibs: String by settings

    repositories {
        maven("https://repo.mineinabyss.com/releases")
    }

    versionCatalogs {
        create("miaLibs").from("com.mineinabyss:catalog:$miaLibs")
    }
}
```

#### Configure your build.gradle

```kotlin
plugins {
    alias(miaLibs.plugins.mia.kotlin.jvm)
    alias(miaLibs.plugins.mia.copyjar)
    alias(miaLibs.plugins.mia.papermc)
    alias(miaLibs.plugins.mia.nms)
    // ...
}

dependencies {
    compileOnly(miaLibs.bundles.idofront.core)
    compileOnly(miaLibs.kotlinx.serialization.json)
    // ...
}
```

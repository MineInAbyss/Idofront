<div align="center">

# idofront-catalog

[![Package](https://badgen.net/maven/v/metadata-url/repo.mineinabyss.com/releases/com/mineinabyss/idofront-catalog/maven-metadata.xml)](https://repo.mineinabyss.com/#/releases/com/mineinabyss/idofront-catalog)
</div>

Grade versions catalog for our plugins. `idofront-catalog-shaded` creates a packaged release that shades all these dependencies.

# Usage

#### settings.gradle.kts

```kotlin
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from("com.mineinabyss:catalog:<version>")
        }
    }
}
```

#### build.gradle.kts

```kotlin
dependencies {
    compileOnly(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.serialization)
    // etc...
}
```

## More info

- [Gradle documentation](https://docs.gradle.org/current/userguide/platforms.html)

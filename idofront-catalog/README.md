<div align="center">

# idofront-catalog
[![Package](https://img.shields.io/maven-metadata/v?metadataUrl=https://repo.mineinabyss.com/releases/com/mineinabyss/catalog/maven-metadata.xml)](https://repo.mineinabyss.com/#/releases/com/mineinabyss/catalog)
</div>

Grade versions catalog for our plugins. `idofront-catalog-shaded` creates a packaged release that shades all these dependencies.

# Usage

#### settings.gradle.kts

```kotlin
dependencyResolutionManagement {
    repositories {
        maven("https://repo.mineinabyss.com/releases")
    }

    versionCatalogs {
        create("libs") {
            from("com.mineinabyss:catalog:<version>")
        }
        
        // See more info section to add your own deps
        create("myownlibs") {
            alias("groovy-core").to("org.codehaus.groovy:groovy:3.0.5")
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

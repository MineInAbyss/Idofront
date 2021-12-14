<div align="center">

# idofront-platform
[![Package](https://badgen.net/maven/v/metadata-url/repo.mineinabyss.com/releases/com/mineinabyss/idofront-platform/maven-metadata.xml)](https://repo.mineinabyss.com/releases/com/mineinabyss/idofront-gradle-platform)
</div>

A Gradle Java-platform for common libraries we use.

## Deps class

When applied through our convention [com.mineinabyss.conventions.platform](https://github.com/MineInAbyss/Idofront/tree/master/idofront-gradle), you may use typesafe groupId variables in the [Deps class](https://github.com/MineInAbyss/Idofront/blob/master/idofront-gradle/src/main/kotlin/com.mineinabyss.conventions.platform.gradle.kts)

### For example
```kotlin
implementation(Deps.kotlinx.serialization.json)
```

## Including dependencies

We include these dependencies at runtime using [idofront-slimjar](https://github.com/MineInAbyss/Idofront/tree/master/idofront-slimjar).
